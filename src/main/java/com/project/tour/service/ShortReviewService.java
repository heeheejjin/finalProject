package com.project.tour.service;

import com.project.tour.controller.DataNotFoundException;
import com.project.tour.domain.*;
import com.project.tour.domain.Package;
import com.project.tour.repository.BookingRepository;
import com.project.tour.repository.PackageRepository;
import com.project.tour.repository.ShortReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ShortReviewService {

    private final ShortReviewRepository shortReviewRepository;

    private final BookingRepository bookingRepository;

    private final PackageRepository packageRepository;


    //텍스트리뷰 작성
    public ShortReview create(String content, Double score, Member member, Package packages){

        ShortReview shortReview = new ShortReview();

        shortReview.setContent(content);
        shortReview.setScore(score);
        shortReview.setCreated(LocalDateTime.now());
        shortReview.setPackages(packages);
        shortReview.setUserName(member);

        return shortReviewRepository.save(shortReview);
    }

    //결제 완료된 userbooking 가져오기
    public List<UserBooking> getBookingShortReview(Long id, int status, Package apackage){

        List<UserBooking> op = bookingRepository.findByMember_IdAndBookingStatusAndApackage(id,status,apackage);

        return op;

    }

    //텍스트리뷰 리스트
    public List<ShortReview> getshortReviewList(Long packageNum){

        Optional<Package> packages = packageRepository.findById(packageNum);

        return shortReviewRepository.findAllByPackagesOrderByIdDesc(packages.get());

    }


    //텍스트 리뷰 수정
    public void update(Long shortReviewNum, Long packageNum,String content){

        ShortReview shortReview = shortReviewRepository.findById(shortReviewNum).get();

        shortReview.setContent(content);

        shortReviewRepository.save(shortReview);


    }


    //텍스트 리뷰 삭제
    public void delete(Long shortReviewNum){

        ShortReview shortReview = shortReviewRepository.findById(shortReviewNum).get();


        shortReviewRepository.delete(shortReview);


    }





}
