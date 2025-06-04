package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Font;
import com.github.lgooddatepicker.components.DatePicker;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.plot.CategoryPlot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import Controller.ReportController;
import java.io.File;
import java.io.FileOutputStream;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.util.List;
import java.util.ArrayList;

public class ReportPanel extends JPanel {
	private DatePicker fromDatePicker, toDatePicker;
	private JComboBox<String> reportTypeComboBox;
	private JButton generateButton;
	private JButton exportPdfButton;
	private JButton emailReportButton;
	private ChartPanel chartPanel;
	private JPanel resultPanel;
	private ReportController reportController = new ReportController();
	private JTable dataTable;
	private DefaultTableModel tableModel;

	/**
	 * Create the panel.
	 */
	public ReportPanel() {
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);
		setBorder(new EmptyBorder(20, 20, 20, 20));

		// Tiêu đề
		JLabel titleLabel = new JLabel("Báo cáo doanh thu");
		titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
		titleLabel.setForeground(new Color(44, 62, 80));
		titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
		add(titleLabel, BorderLayout.NORTH);

		// Panel bộ lọc
		JPanel filterPanel = createFilterPanel();
		add(filterPanel, BorderLayout.NORTH);

		// Panel kết quả
		resultPanel = new JPanel(new BorderLayout());
		resultPanel.setBackground(Color.WHITE);
		
		// Tạo biểu đồ mẫu
		createSampleChart();
		resultPanel.add(chartPanel, BorderLayout.CENTER);
		
		add(resultPanel, BorderLayout.CENTER);

		// Xử lý sự kiện
		setupEventHandlers();
	}

	private JPanel createFilterPanel() {
		JPanel filterPanel = new JPanel(new GridBagLayout());
		filterPanel.setBackground(Color.WHITE);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, 10, 0, 10);
		gbc.anchor = GridBagConstraints.WEST;

		// Từ ngày
		gbc.gridx = 0; gbc.gridy = 0;
		filterPanel.add(new JLabel("Từ ngày:"), gbc);
		fromDatePicker = new DatePicker();
		fromDatePicker.setPreferredSize(new Dimension(130, 30));
		gbc.gridx = 1;
		filterPanel.add(fromDatePicker, gbc);

		// Đến ngày
		gbc.gridx = 2;
		filterPanel.add(new JLabel("Đến ngày:"), gbc);
		toDatePicker = new DatePicker();
		toDatePicker.setPreferredSize(new Dimension(130, 30));
		gbc.gridx = 3;
		filterPanel.add(toDatePicker, gbc);

		// Loại báo cáo
		gbc.gridx = 4;
		filterPanel.add(new JLabel("Loại báo cáo:"), gbc);
		reportTypeComboBox = new JComboBox<>(new String[]{
			"Theo ngày", 
			"Theo phim", 
			"Theo suất chiếu",
			"Theo phòng chiếu",
			"Theo loại vé",
			"Theo khách hàng"
		});
		reportTypeComboBox.setPreferredSize(new Dimension(140, 30));
		gbc.gridx = 5;
		filterPanel.add(reportTypeComboBox, gbc);

		// Nút tạo báo cáo
		generateButton = createStyledButton("Tạo báo cáo", new Color(41, 128, 185));
		gbc.gridx = 6;
		filterPanel.add(generateButton, gbc);

		// Nút xuất PDF
		exportPdfButton = createStyledButton("Xuất PDF", new Color(231, 76, 60));
		gbc.gridx = 7;
		filterPanel.add(exportPdfButton, gbc);

		// Nút gửi email
		emailReportButton = createStyledButton("Gửi Email", new Color(155, 89, 182));
		gbc.gridx = 8;
		filterPanel.add(emailReportButton, gbc);

		filterPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
		return filterPanel;
	}

	private void createDataTable() {
		String[] columnNames = {"Ngày", "Doanh thu", "Số vé", "Tỷ lệ ghế ngồi"};
		tableModel = new DefaultTableModel(columnNames, 0);
		dataTable = new JTable(tableModel);
		dataTable.setFont(new Font("Arial", Font.PLAIN, 14));
		dataTable.setRowHeight(30);
		dataTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
		dataTable.getTableHeader().setBackground(new Color(240, 240, 240));
	}

	private JButton createStyledButton(String text, Color bgColor) {
		JButton button = new JButton(text);
		button.setBackground(bgColor);
		button.setForeground(Color.BLACK);
		button.setFont(new Font("Arial", Font.BOLD, 14));
		button.setFocusPainted(false);
		button.setBorder(new EmptyBorder(8, 20, 8, 20));
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		button.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				button.setBackground(bgColor.darker());
			}
			
			public void mouseExited(java.awt.event.MouseEvent evt) {
				button.setBackground(bgColor);
			}
		});
		
		return button;
	}

	private void setupEventHandlers() {
		generateButton.addActionListener(e -> updateReport());
		exportPdfButton.addActionListener(e -> exportToPdf());
		emailReportButton.addActionListener(e -> sendEmailReport());
	}

	private void updateReport() {
		java.time.LocalDate fromLocal = fromDatePicker.getDate();
		java.time.LocalDate toLocal = toDatePicker.getDate();
		if (fromLocal == null || toLocal == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn đủ ngày bắt đầu và kết thúc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}

		String reportType = (String) reportTypeComboBox.getSelectedItem();
		List<Object[]> data = new ArrayList<>();
		
		switch (reportType) {
			case "Theo ngày":
				data = reportController.getRevenueByDateRange(fromLocal, toLocal);
				break;
			case "Theo phim":
				data = reportController.getRevenueByMovie(fromLocal, toLocal);
				break;
			case "Theo suất chiếu":
				data = reportController.getRevenueByShowtime(fromLocal, toLocal);
				break;
			case "Theo phòng chiếu":
				data = reportController.getRevenueByRoom(fromLocal, toLocal);
				break;
			case "Theo loại vé":
				data = reportController.getRevenueByTicketType(fromLocal, toLocal);
				break;
			case "Theo khách hàng":
				data = reportController.getCustomerStatistics(fromLocal, toLocal);
				break;
			default:
				data = reportController.getAllStatistics(fromLocal, toLocal);
		}

		updateChart(data, reportType);
	}

	private void updateChart(List<Object[]> data, String reportType) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (Object[] row : data) {
			double revenue;
			if (row[1] instanceof String) {
				try {
					// Loại bỏ ký tự đặc biệt và chuyển đổi sang số
					String revenueStr = ((String) row[1]).replaceAll("[^\\d.]", "");
					revenue = Double.parseDouble(revenueStr);
				} catch (NumberFormatException e) {
					// Nếu không thể chuyển đổi, bỏ qua dòng này
					continue;
				}
			} else {
				revenue = (Double) row[1];
			}
			dataset.addValue(revenue, "Doanh thu", row[0].toString());
		}

		String title = "Biểu đồ doanh thu " + reportType.toLowerCase();
		String xAxisLabel = "Thời gian";
		
		// Điều chỉnh nhãn trục X dựa trên loại báo cáo
		switch (reportType) {
			case "Theo phim":
				xAxisLabel = "Phim";
				break;
			case "Theo suất chiếu":
				xAxisLabel = "Suất chiếu";
				break;
			case "Theo phòng chiếu":
				xAxisLabel = "Phòng chiếu";
				break;
			case "Theo loại vé":
				xAxisLabel = "Loại vé";
				break;
			case "Theo khách hàng":
				xAxisLabel = "Khách hàng";
				break;
		}

		JFreeChart chart = ChartFactory.createLineChart(
			title,
			xAxisLabel,
			"Doanh thu (VNĐ)",
			dataset,
			PlotOrientation.VERTICAL,
			true,
			true,
			false
		);

		customizeChart(chart);
		
		resultPanel.remove(chartPanel);
		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(900, 500));
		resultPanel.add(chartPanel, BorderLayout.CENTER);
		resultPanel.revalidate();
		resultPanel.repaint();
	}

	private void customizeChart(JFreeChart chart) {
		chart.setBackgroundPaint(Color.WHITE);
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setRangeGridlinePaint(Color.GRAY);
		plot.setOutlinePaint(null);
		
		org.jfree.chart.renderer.category.LineAndShapeRenderer renderer = 
			new org.jfree.chart.renderer.category.LineAndShapeRenderer();
		renderer.setSeriesPaint(0, new Color(41, 128, 185));
		renderer.setSeriesStroke(0, new java.awt.BasicStroke(3.0f));
		renderer.setSeriesShapesVisible(0, true);
		plot.setRenderer(renderer);
	}

	private void exportToPdf() {
		try {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Chọn vị trí lưu file PDF");
			fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF files (*.pdf)", "pdf"));
			fileChooser.setSelectedFile(new File("report.pdf"));
			
			int userSelection = fileChooser.showSaveDialog(this);
			if (userSelection == JFileChooser.APPROVE_OPTION) {
				File fileToSave = fileChooser.getSelectedFile();
				// Thêm đuôi .pdf nếu chưa có
				if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
					fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
				}
				
				Document document = new Document();
				PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
				document.open();
				
				// Thêm tiêu đề
				com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD);
				Paragraph title = new Paragraph("Báo cáo doanh thu", titleFont);
				title.setAlignment(Element.ALIGN_CENTER);
				document.add(title);
				
				// Thêm biểu đồ
				// TODO: Thêm biểu đồ vào PDF
				
				document.close();
				
				JOptionPane.showMessageDialog(this, "Xuất PDF thành công!\nFile được lưu tại: " + fileToSave.getAbsolutePath());
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Lỗi khi xuất PDF: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void sendEmailReport() {
		// TODO: Implement email sending functionality
		JOptionPane.showMessageDialog(this, "Tính năng gửi email đang được phát triển!");
	}

	private void createSampleChart() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		dataset.addValue(1500000, "Doanh thu", "T2");
		dataset.addValue(2500000, "Doanh thu", "T3");
		dataset.addValue(2000000, "Doanh thu", "T4");
		dataset.addValue(3000000, "Doanh thu", "T5");
		dataset.addValue(2800000, "Doanh thu", "T6");
		dataset.addValue(3500000, "Doanh thu", "T7");
		dataset.addValue(4000000, "Doanh thu", "CN");

		JFreeChart chart = ChartFactory.createLineChart(
			"Biểu đồ doanh thu theo ngày",
			"Ngày",
			"Doanh thu (VNĐ)",
			dataset,
			PlotOrientation.VERTICAL,
			true,
			true,
			false
		);

		customizeChart(chart);
		
		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(900, 500));
	}
}
