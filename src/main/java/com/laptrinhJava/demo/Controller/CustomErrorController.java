//package com.laptrinhJava.demo.Controller;
//
//import jakarta.servlet.RequestDispatcher;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.boot.web.servlet.error.ErrorController;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@Controller
//public class CustomErrorController implements ErrorController {
//
//    @RequestMapping("/error")
//    public String handleError(HttpServletRequest request) {
//        // Lấy mã trạng thái lỗi
//        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
//        // Lấy thông điệp lỗi
//        Object errorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
//
//        // Xử lý từng trường hợp lỗi cụ thể
//        if (status != null) {
//            int statusCode = Integer.parseInt(status.toString());
//
//            // Xử lý lỗi 403 dựa trên thông điệp (message)
//            if (errorMessage != null && errorMessage.toString().contains("403")) {
//                return "error/403"; // Trang lỗi 403 tùy chỉnh của bạn
//            }
//
//            // Xử lý lỗi 404 dựa trên thông điệp (message)
//            if (statusCode == 404 ) {
//                return "error/404"; // Trang lỗi 404 tùy chỉnh của bạn
//            }
//        }
//
//        // Đối với các lỗi khác, chuyển hướng đến trang lỗi chung
//        return "error/generic"; // Tạo một trang lỗi chung
//    }
//
//    public String getErrorPath() {
//        return "/error";
//    }
//}