package com.project.tour.service;

import com.project.tour.domain.*;
import com.project.tour.repository.MemberRepository;
import com.project.tour.repository.PayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PayService {

    private final PayRepository payRepository;
    private final MemberRepository memberRepository;

    //pay테이블 저장하기
    public Pay create(UserBooking userBooking, Member member, PayForm payForm){

        Pay pay = new Pay();

        pay.setUserBooking(userBooking);
        pay.setMember(member);
        pay.setPayDate(LocalDateTime.now());
        pay.setPayInfo(payForm.getPayInfo());
        pay.setPayTotalPrice(payForm.getTotalPrice());
        pay.setPayMethod(payForm.getPayMethod());
        pay.setUsedPoint(payForm.getUsedPoint());

        return payRepository.save(pay);
    }

    //
//    public long getPayNum(Member member, LocalDateTime payDate){
//
//        long payNum = payRepository.findByMemberAndPayDate(member, payDate).get().getId();
//
//        return payNum;
//    }

    //member가 예약한 정보중에 젤 최근에 예약한 정보 가져오기
    public  Pay getRecentPay(){

        long id = payRepository.maxPayNum();
        Optional<Pay> result = payRepository.findById(id);

        return result.get();
    }

    public Pay getPay(long payNum){

        Optional<Pay> result = payRepository.findById(payNum);

        return result.get();
    }

    public List<Pay> findAll (){

        return payRepository.findAll();

    }


    public void getPoint(Member member,int payTotalCount, int usingPoint){

        int point = (int)Math.round(payTotalCount * 0.05); //포인트 적립
        int nowPoint = member.getPoint()+point-usingPoint;

        member.setPoint(nowPoint); //포인트 정리
        memberRepository.save(member);


    }

    //결제 취소
    public void delete(Long id){

        Pay pay = payRepository.findById(id).get();
        payRepository.delete(pay);
    }

    public void rePoint(Member member, Long id){

        Optional<Pay> pay = payRepository.findById(id);

        //결제시 적립해줬던 포인트
        int point = (int)Math.round(pay.get().getPayTotalPrice() * 0.05);

        member.setPoint(member.getPoint()+pay.get().getUsedPoint()-point);

        memberRepository.save(member);
    }

}
