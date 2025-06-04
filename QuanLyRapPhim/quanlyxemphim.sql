-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1
-- Thời gian đã tạo: Th6 02, 2025 lúc 08:24 PM
-- Phiên bản máy phục vụ: 10.4.32-MariaDB
-- Phiên bản PHP: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `quanlyxemphim`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `bookingdetails`
--

CREATE TABLE `bookingdetails` (
  `booking_detail_id` int(11) NOT NULL,
  `booking_id` int(11) DEFAULT NULL,
  `ticket_id` int(11) DEFAULT NULL,
  `unit_price` decimal(10,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `bookingdetails`
--

INSERT INTO `bookingdetails` (`booking_detail_id`, `booking_id`, `ticket_id`, `unit_price`) VALUES
(1, 1, 1, 100000.00),
(2, 2, 2, 100000.00),
(3, 3, 3, 100000.00),
(4, 4, 4, 100000.00),
(5, 5, 5, 100000.00),
(6, 6, 6, 100000.00),
(7, 7, 7, 100000.00),
(8, 8, 8, 100000.00),
(9, 9, 9, 100000.00),
(10, 10, 10, 100000.00);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `bookings`
--

CREATE TABLE `bookings` (
  `booking_id` int(11) NOT NULL,
  `customer_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `booking_time` datetime DEFAULT current_timestamp(),
  `status` enum('pending','confirmed','paid','cancelled') DEFAULT 'pending'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `bookings`
--

INSERT INTO `bookings` (`booking_id`, `customer_id`, `user_id`, `booking_time`, `status`) VALUES
(1, 1, 1, '2025-06-03 01:19:12', 'paid'),
(2, 2, 1, '2025-05-15 14:30:00', 'paid'),
(3, 3, 5, '2025-05-20 09:15:00', 'paid'),
(4, 4, 5, '2025-05-25 16:45:00', 'paid'),
(5, 5, 1, '2025-06-01 11:20:00', 'paid'),
(6, 6, 5, '2025-06-02 13:10:00', 'paid'),
(7, 7, 1, '2025-05-10 10:00:00', 'paid'),
(8, 8, 5, '2025-05-18 15:30:00', 'paid'),
(9, 9, 1, '2025-05-28 17:45:00', 'paid'),
(10, 10, 5, '2025-06-04 14:00:00', 'paid');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `customers`
--

CREATE TABLE `customers` (
  `customer_id` int(11) NOT NULL,
  `full_name` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `is_deleted` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `customers`
--

INSERT INTO `customers` (`customer_id`, `full_name`, `email`, `phone`, `created_at`, `is_deleted`) VALUES
(1, 'Nguyen Van A', 'nguyenvana@email.com', '0123456789', '2025-06-03 01:18:02', 0),
(2, 'Tran Thi B', 'tranthib@email.com', '0987654321', '2025-06-03 01:18:02', 0),
(3, 'Le Van C', 'levanc@email.com', '0123987456', '2025-06-03 01:18:02', 0),
(4, 'Pham Thi D', 'phamthid@email.com', '0123456788', '2025-06-03 01:18:02', 0),
(5, 'Hoang Van E', 'hoangvane@email.com', '0987654322', '2025-06-03 01:18:02', 0),
(6, 'Vu Thi F', 'vuthif@email.com', '0123987457', '2025-06-03 01:18:02', 0),
(7, 'Dang Van G', 'dangvang@email.com', '0123456787', '2025-06-03 01:18:02', 0),
(8, 'Bui Thi H', 'buithih@email.com', '0987654323', '2025-06-03 01:18:02', 0),
(9, 'Do Van I', 'dovani@email.com', '0123987458', '2025-06-03 01:18:02', 0),
(10, 'Ngo Thi K', 'ngothik@email.com', '0123456786', '2025-06-03 01:18:02', 0),
(11, 'Ly Van L', 'lyvanl@email.com', '0987654324', '2025-06-03 01:18:02', 0),
(12, 'Mai Thi M', 'maithim@email.com', '0123987459', '2025-06-03 01:18:02', 0),
(13, 'Dao Van N', 'daovann@email.com', '0123456785', '2025-06-03 01:18:02', 0),
(14, 'Ho Thi O', 'hothio@email.com', '0987654325', '2025-06-03 01:18:02', 0),
(15, 'Duong Van P', 'duongvanp@email.com', '0123987460', '2025-06-03 01:18:02', 0),
(16, 'Truong Thi Q', 'truongthiq@email.com', '0123456784', '2025-06-03 01:18:02', 0),
(17, 'Nguyen Van R', 'nguyenvanr@email.com', '0987654326', '2025-06-03 01:18:02', 0),
(18, 'Tran Thi S', 'tranthis@email.com', '0123987461', '2025-06-03 01:18:02', 0),
(19, 'Le Van T', 'levant@email.com', '0123456783', '2025-06-03 01:18:02', 0),
(20, 'Pham Thi U', 'phamthiu@email.com', '0987654327', '2025-06-03 01:18:02', 0);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `movies`
--

CREATE TABLE `movies` (
  `movie_id` int(11) NOT NULL,
  `title` varchar(100) NOT NULL,
  `genre` varchar(50) DEFAULT NULL,
  `duration` int(11) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `trailer_url` varchar(255) DEFAULT NULL,
  `release_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `movies`
--

INSERT INTO `movies` (`movie_id`, `title`, `genre`, `duration`, `description`, `trailer_url`, `release_date`) VALUES
(1, 'Avengers: Secret Wars', 'Action', 180, 'The epic conclusion to the Avengers saga', 'https://youtube.com/avengers', '2024-05-18'),
(2, 'The Batman 2', 'Action', 160, 'The Dark Knight returns', 'https://youtube.com/batman2', '2024-05-20'),
(3, 'Dune: Part Two', 'Sci-Fi', 166, 'The epic continues', 'https://youtube.com/dune2', '2024-05-22'),
(4, 'Deadpool 3', 'Action', 150, 'The Merc with a Mouth returns', 'https://youtube.com/deadpool3', '2024-05-24'),
(5, 'Joker 2', 'Drama', 140, 'The origin story continues', 'https://youtube.com/joker2', '2024-05-26'),
(6, 'Mission Impossible 8', 'Action', 170, 'Tom Cruise returns', 'https://youtube.com/mi8', '2024-05-28'),
(7, 'Fast & Furious 11', 'Action', 145, 'The final ride', 'https://youtube.com/ff11', '2024-05-30'),
(8, 'Avatar 3', 'Sci-Fi', 190, 'Return to Pandora', 'https://youtube.com/avatar3', '2024-06-01'),
(9, 'Black Panther 3', 'Action', 155, 'Wakanda Forever', 'https://youtube.com/bp3', '2024-06-03'),
(10, 'Spider-Man 4', 'Action', 145, 'Web-slinging adventure', 'https://youtube.com/spiderman4', '2024-06-05');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `payments`
--

CREATE TABLE `payments` (
  `payment_id` int(11) NOT NULL,
  `booking_id` int(11) DEFAULT NULL,
  `amount` decimal(10,2) DEFAULT NULL,
  `payment_method` enum('cash','credit_card','momo','bank_transfer') DEFAULT NULL,
  `payment_time` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `payments`
--

INSERT INTO `payments` (`payment_id`, `booking_id`, `amount`, `payment_method`, `payment_time`) VALUES
(1, 1, 100000.00, 'cash', '2025-06-03 01:19:40'),
(2, 2, 100000.00, 'credit_card', '2025-05-15 14:35:00'),
(3, 3, 100000.00, 'momo', '2025-05-20 09:20:00'),
(4, 4, 100000.00, 'bank_transfer', '2025-05-25 16:50:00'),
(5, 5, 100000.00, 'cash', '2025-06-01 11:25:00'),
(6, 6, 100000.00, 'credit_card', '2025-06-02 13:15:00'),
(7, 7, 100000.00, 'momo', '2025-05-10 10:05:00'),
(8, 8, 100000.00, 'bank_transfer', '2025-05-18 15:35:00'),
(9, 9, 100000.00, 'cash', '2025-05-28 17:50:00'),
(10, 10, 100000.00, 'credit_card', '2025-06-04 14:05:00');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `promotiondetails`
--

CREATE TABLE `promotiondetails` (
  `promo_detail_id` int(11) NOT NULL,
  `promotion_id` int(11) DEFAULT NULL,
  `booking_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `promotions`
--

CREATE TABLE `promotions` (
  `promotion_id` int(11) NOT NULL,
  `code` varchar(20) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `discount_percent` int(11) DEFAULT NULL,
  `valid_from` date DEFAULT NULL,
  `valid_to` date DEFAULT NULL,
  `min_total` decimal(10,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `promotions`
--

INSERT INTO `promotions` (`promotion_id`, `code`, `description`, `discount_percent`, `valid_from`, `valid_to`, `min_total`) VALUES
(1, 'SUMMER2024', 'Summer promotion 20% off', 20, '2024-06-01', '2024-08-31', 200000.00),
(2, 'WELCOME10', 'Welcome discount 10%', 10, '2024-01-01', '2024-12-31', 100000.00),
(3, 'WEEKEND15', 'Weekend special 15% off', 15, '2024-01-01', '2024-12-31', 150000.00),
(4, 'STUDENT20', 'Student discount 20% off', 20, '2024-01-01', '2024-12-31', 100000.00),
(5, 'FAMILY25', 'Family package 25% off', 25, '2024-01-01', '2024-12-31', 300000.00);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `rooms`
--

CREATE TABLE `rooms` (
  `room_id` int(11) NOT NULL,
  `room_name` varchar(50) NOT NULL,
  `total_seats` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `rooms`
--

INSERT INTO `rooms` (`room_id`, `room_name`, `total_seats`) VALUES
(1, 'Chiếu 1', 30),
(4, 'Chiếu 2', 30),
(5, 'Chiếu 3', 30),
(6, 'Chiếu 4', 100),
(7, 'Chiếu 5', 80);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `seats`
--

CREATE TABLE `seats` (
  `seat_id` int(11) NOT NULL,
  `room_id` int(11) DEFAULT NULL,
  `seat_number` varchar(10) DEFAULT NULL,
  `status` enum('available','booked','occupied') DEFAULT 'available'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `seats`
--

INSERT INTO `seats` (`seat_id`, `room_id`, `seat_number`, `status`) VALUES
(1, 1, 'A1', 'available'),
(2, 1, 'A2', 'available'),
(3, 1, 'A3', 'available'),
(4, 1, 'A4', 'available'),
(5, 1, 'A5', 'available'),
(6, 1, 'B1', 'available'),
(7, 1, 'B2', 'available'),
(8, 1, 'B3', 'available'),
(9, 1, 'B4', 'available'),
(10, 1, 'B5', 'available'),
(11, 1, 'C1', 'available'),
(12, 1, 'C2', 'available'),
(13, 1, 'C3', 'available'),
(14, 1, 'C4', 'available'),
(15, 1, 'C5', 'available'),
(16, 1, 'D1', 'available'),
(17, 1, 'D2', 'available'),
(18, 1, 'D3', 'available'),
(19, 1, 'D4', 'available'),
(20, 1, 'D5', 'available'),
(21, 1, 'E1', 'available'),
(22, 1, 'E2', 'available'),
(23, 1, 'E3', 'available'),
(24, 1, 'E4', 'available'),
(25, 1, 'E5', 'available'),
(26, 4, 'A1', 'available'),
(27, 4, 'A2', 'available'),
(28, 4, 'A3', 'available'),
(29, 4, 'A4', 'available'),
(30, 4, 'A5', 'available'),
(31, 4, 'B1', 'available'),
(32, 4, 'B2', 'available'),
(33, 4, 'B3', 'available'),
(34, 4, 'B4', 'available'),
(35, 4, 'B5', 'available'),
(36, 4, 'C1', 'available'),
(37, 4, 'C2', 'available'),
(38, 4, 'C3', 'available'),
(39, 4, 'C4', 'available'),
(40, 4, 'C5', 'available'),
(41, 4, 'D1', 'available'),
(42, 4, 'D2', 'available'),
(43, 4, 'D3', 'available'),
(44, 4, 'D4', 'available'),
(45, 4, 'D5', 'available'),
(46, 4, 'E1', 'available'),
(47, 4, 'E2', 'available'),
(48, 4, 'E3', 'available'),
(49, 4, 'E4', 'available'),
(50, 4, 'E5', 'available'),
(51, 5, 'A1', 'available'),
(52, 5, 'A2', 'available'),
(53, 5, 'A3', 'available'),
(54, 5, 'A4', 'available'),
(55, 5, 'A5', 'available'),
(56, 5, 'B1', 'available'),
(57, 5, 'B2', 'available'),
(58, 5, 'B3', 'available'),
(59, 5, 'B4', 'available'),
(60, 5, 'B5', 'available'),
(61, 5, 'C1', 'available'),
(62, 5, 'C2', 'available'),
(63, 5, 'C3', 'available'),
(64, 5, 'C4', 'available'),
(65, 5, 'C5', 'available'),
(66, 5, 'D1', 'available'),
(67, 5, 'D2', 'available'),
(68, 5, 'D3', 'available'),
(69, 5, 'D4', 'available'),
(70, 5, 'D5', 'available'),
(71, 5, 'E1', 'available'),
(72, 5, 'E2', 'available'),
(73, 5, 'E3', 'available'),
(74, 5, 'E4', 'available'),
(75, 5, 'E5', 'available'),
(76, 6, 'A1', 'available'),
(77, 6, 'A2', 'available'),
(78, 6, 'A3', 'available'),
(79, 6, 'A4', 'available'),
(80, 6, 'A5', 'available'),
(81, 6, 'B1', 'available'),
(82, 6, 'B2', 'available'),
(83, 6, 'B3', 'available'),
(84, 6, 'B4', 'available'),
(85, 6, 'B5', 'available'),
(86, 6, 'C1', 'available'),
(87, 6, 'C2', 'available'),
(88, 6, 'C3', 'available'),
(89, 6, 'C4', 'available'),
(90, 6, 'C5', 'available'),
(91, 6, 'D1', 'available'),
(92, 6, 'D2', 'available'),
(93, 6, 'D3', 'available'),
(94, 6, 'D4', 'available'),
(95, 6, 'D5', 'available'),
(96, 6, 'E1', 'available'),
(97, 6, 'E2', 'available'),
(98, 6, 'E3', 'available'),
(99, 6, 'E4', 'available'),
(100, 6, 'E5', 'available'),
(101, 7, 'A1', 'available'),
(102, 7, 'A2', 'available'),
(103, 7, 'A3', 'available'),
(104, 7, 'A4', 'available'),
(105, 7, 'A5', 'available'),
(106, 7, 'B1', 'available'),
(107, 7, 'B2', 'available'),
(108, 7, 'B3', 'available'),
(109, 7, 'B4', 'available'),
(110, 7, 'B5', 'available'),
(111, 7, 'C1', 'available'),
(112, 7, 'C2', 'available'),
(113, 7, 'C3', 'available'),
(114, 7, 'C4', 'available'),
(115, 7, 'C5', 'available'),
(116, 7, 'D1', 'available'),
(117, 7, 'D2', 'available'),
(118, 7, 'D3', 'available'),
(119, 7, 'D4', 'available'),
(120, 7, 'D5', 'available'),
(121, 7, 'E1', 'available'),
(122, 7, 'E2', 'available'),
(123, 7, 'E3', 'available'),
(124, 7, 'E4', 'available'),
(125, 7, 'E5', 'available');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `showtimes`
--

CREATE TABLE `showtimes` (
  `showtime_id` int(11) NOT NULL,
  `movie_id` int(11) DEFAULT NULL,
  `room_id` int(11) DEFAULT NULL,
  `show_date` date DEFAULT NULL,
  `start_time` time DEFAULT NULL,
  `end_time` time DEFAULT NULL,
  `base_price` decimal(10,2) NOT NULL DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `showtimes`
--

INSERT INTO `showtimes` (`showtime_id`, `movie_id`, `room_id`, `show_date`, `start_time`, `end_time`, `base_price`) VALUES
(1, 2, 1, '2025-06-03', '01:16:51', '01:16:51', 100000.00),
(2, 1, 1, '2025-06-04', '01:17:17', '01:17:17', 100000.00);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `tickets`
--

CREATE TABLE `tickets` (
  `ticket_id` int(11) NOT NULL,
  `showtime_id` int(11) DEFAULT NULL,
  `seat_id` int(11) DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `status` enum('available','booked','sold') DEFAULT 'available'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `tickets`
--

INSERT INTO `tickets` (`ticket_id`, `showtime_id`, `seat_id`, `price`, `status`) VALUES
(1, 2, 1, 100000.00, 'sold'),
(2, 1, 2, 100000.00, 'sold'),
(3, 2, 3, 100000.00, 'sold'),
(4, 1, 4, 100000.00, 'sold'),
(5, 2, 5, 100000.00, 'sold'),
(6, 1, 6, 100000.00, 'sold'),
(7, 2, 7, 100000.00, 'sold'),
(8, 1, 8, 100000.00, 'sold'),
(9, 2, 9, 100000.00, 'sold'),
(10, 1, 10, 100000.00, 'sold');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `role` enum('admin','staff') DEFAULT 'staff',
  `full_name` varchar(100) DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `users`
--

INSERT INTO `users` (`user_id`, `username`, `password`, `role`, `full_name`, `created_at`) VALUES
(1, 'admin', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'admin', 'Nguyen Van Duoc', '2025-05-10 09:02:35'),
(5, 'nv1', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'staff', 'Nguyen Van Duoc', '2025-05-15 00:00:00');

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `bookingdetails`
--
ALTER TABLE `bookingdetails`
  ADD PRIMARY KEY (`booking_detail_id`),
  ADD KEY `booking_id` (`booking_id`),
  ADD KEY `ticket_id` (`ticket_id`);

--
-- Chỉ mục cho bảng `bookings`
--
ALTER TABLE `bookings`
  ADD PRIMARY KEY (`booking_id`),
  ADD KEY `customer_id` (`customer_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Chỉ mục cho bảng `customers`
--
ALTER TABLE `customers`
  ADD PRIMARY KEY (`customer_id`);

--
-- Chỉ mục cho bảng `movies`
--
ALTER TABLE `movies`
  ADD PRIMARY KEY (`movie_id`);

--
-- Chỉ mục cho bảng `payments`
--
ALTER TABLE `payments`
  ADD PRIMARY KEY (`payment_id`),
  ADD KEY `booking_id` (`booking_id`);

--
-- Chỉ mục cho bảng `promotiondetails`
--
ALTER TABLE `promotiondetails`
  ADD PRIMARY KEY (`promo_detail_id`),
  ADD KEY `promotion_id` (`promotion_id`),
  ADD KEY `booking_id` (`booking_id`);

--
-- Chỉ mục cho bảng `promotions`
--
ALTER TABLE `promotions`
  ADD PRIMARY KEY (`promotion_id`),
  ADD UNIQUE KEY `code` (`code`);

--
-- Chỉ mục cho bảng `rooms`
--
ALTER TABLE `rooms`
  ADD PRIMARY KEY (`room_id`);

--
-- Chỉ mục cho bảng `seats`
--
ALTER TABLE `seats`
  ADD PRIMARY KEY (`seat_id`),
  ADD KEY `room_id` (`room_id`);

--
-- Chỉ mục cho bảng `showtimes`
--
ALTER TABLE `showtimes`
  ADD PRIMARY KEY (`showtime_id`),
  ADD KEY `movie_id` (`movie_id`),
  ADD KEY `room_id` (`room_id`);

--
-- Chỉ mục cho bảng `tickets`
--
ALTER TABLE `tickets`
  ADD PRIMARY KEY (`ticket_id`),
  ADD KEY `showtime_id` (`showtime_id`),
  ADD KEY `seat_id` (`seat_id`);

--
-- Chỉ mục cho bảng `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `bookingdetails`
--
ALTER TABLE `bookingdetails`
  MODIFY `booking_detail_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT cho bảng `bookings`
--
ALTER TABLE `bookings`
  MODIFY `booking_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT cho bảng `customers`
--
ALTER TABLE `customers`
  MODIFY `customer_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT cho bảng `movies`
--
ALTER TABLE `movies`
  MODIFY `movie_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT cho bảng `payments`
--
ALTER TABLE `payments`
  MODIFY `payment_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT cho bảng `promotiondetails`
--
ALTER TABLE `promotiondetails`
  MODIFY `promo_detail_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT cho bảng `promotions`
--
ALTER TABLE `promotions`
  MODIFY `promotion_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT cho bảng `rooms`
--
ALTER TABLE `rooms`
  MODIFY `room_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT cho bảng `seats`
--
ALTER TABLE `seats`
  MODIFY `seat_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=126;

--
-- AUTO_INCREMENT cho bảng `showtimes`
--
ALTER TABLE `showtimes`
  MODIFY `showtime_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT cho bảng `tickets`
--
ALTER TABLE `tickets`
  MODIFY `ticket_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT cho bảng `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `bookingdetails`
--
ALTER TABLE `bookingdetails`
  ADD CONSTRAINT `bookingdetails_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`booking_id`),
  ADD CONSTRAINT `bookingdetails_ibfk_2` FOREIGN KEY (`ticket_id`) REFERENCES `tickets` (`ticket_id`);

--
-- Các ràng buộc cho bảng `bookings`
--
ALTER TABLE `bookings`
  ADD CONSTRAINT `bookings_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
  ADD CONSTRAINT `bookings_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Các ràng buộc cho bảng `payments`
--
ALTER TABLE `payments`
  ADD CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`booking_id`);

--
-- Các ràng buộc cho bảng `promotiondetails`
--
ALTER TABLE `promotiondetails`
  ADD CONSTRAINT `promotiondetails_ibfk_1` FOREIGN KEY (`promotion_id`) REFERENCES `promotions` (`promotion_id`),
  ADD CONSTRAINT `promotiondetails_ibfk_2` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`booking_id`);

--
-- Các ràng buộc cho bảng `seats`
--
ALTER TABLE `seats`
  ADD CONSTRAINT `seats_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`room_id`);

--
-- Các ràng buộc cho bảng `showtimes`
--
ALTER TABLE `showtimes`
  ADD CONSTRAINT `showtimes_ibfk_1` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`movie_id`),
  ADD CONSTRAINT `showtimes_ibfk_2` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`room_id`);

--
-- Các ràng buộc cho bảng `tickets`
--
ALTER TABLE `tickets`
  ADD CONSTRAINT `tickets_ibfk_1` FOREIGN KEY (`showtime_id`) REFERENCES `showtimes` (`showtime_id`),
  ADD CONSTRAINT `tickets_ibfk_2` FOREIGN KEY (`seat_id`) REFERENCES `seats` (`seat_id`);

DELIMITER $$
--
-- Sự kiện
--
CREATE DEFINER=`root`@`localhost` EVENT `delete_old_showtimes` ON SCHEDULE EVERY 1 DAY STARTS '2025-05-19 08:16:46' ON COMPLETION NOT PRESERVE ENABLE DO DELETE FROM Showtimes
  WHERE show_date < CURDATE()$$

DELIMITER ;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
