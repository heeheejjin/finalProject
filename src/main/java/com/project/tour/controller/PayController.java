package com.project.tour.controller;

import com.project.tour.domain.Member;
import com.project.tour.domain.PayForm;
import com.project.tour.domain.UserBooking;
import com.project.tour.domain.UserBookingForm;
import com.project.tour.service.MemberService;
import com.project.tour.service.PayService;
import com.project.tour.service.UserBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pay")
public class PayController {

    private final UserBookingService userBookingService;
    private final PayService payService;
    private final MemberService memberService;

    @GetMapping
    public String getPay(Model model, Principal principal, PayForm payForm, UserBookingForm userBookingForm){

        long bookingNum = 1; //테스트용 코드 마이페이지에서 결제대기상태를 누르면 가지고 오게

        //userBooking 정보 넘기기
        UserBooking userBooking = userBookingService.getUserBooking(bookingNum);
        model.addAttribute("userBooking",userBooking);

        return "booking-pay/payment";
    }

    @PostMapping("/confirmation/{id}")
    public String paying(@Validated PayForm payForm, @Validated UserBookingForm userBookingForm, BindingResult bindingResult,
                         Principal principal, Model model, @PathVariable("id") Long id){

        if(bindingResult.hasErrors()){
            return "booking-pay/booking";
        }

        //데이터 저장할 때 넘길 정보 : userbooking,member
        UserBooking userBooking = userBookingService.getUserBooking(id);
        Member member = memberService.getMember(principal.getName());

        //1. pay 테이블 데이터 저장
        payService.create(userBooking, member, payForm);

        //2. userBooking 테이블 bookingStatus 데이터 수정
        userBookingForm.setBookingStatus(2); //form 예약 상태 변경
        userBookingService.modifyBookingStatus(userBooking, userBookingForm);

        return "main";
    }

}