package com.project.tour.controller;

import com.project.tour.domain.*;
import com.project.tour.domain.Package;
import com.project.tour.service.*;
import com.project.tour.util.FileUploadUtil;
import com.project.tour.util.PackageFileUpload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminPackageService adminPackageService;

    private final AdminPackageDateService adminPackageDateService;

    private final UserBookingService userBookingService;

    private final PayService payService;

    private final MemberService memberService;

    private final AdminSalesService adminSalesService;

    private final MypageService mypageService;

    private final SalesExcelService salesExcelService;



    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/main")
    public String admin_main() {
        return "admin/admin_Main";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/booking")
    public String admin_booking(
            @RequestParam(value = "packageno", required = false) Long packageNum,
            Model model, @PageableDefault Pageable pageable) {

        Page<PackageDate> paging = adminPackageDateService.getList(packageNum,pageable);
        model.addAttribute("paging",paging);
        // model.addAttribute("date",packageDate);

        return "admin/admin_Booking";
    }


    //?????? ?????? ??????
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/bookingUser")
    public String admin_bookingUser(Model model, @PageableDefault Pageable pageable,Package aPackage,PackageDate packageDate,Member member) {

        Page<UserBooking> paging = userBookingService.getBookingList(pageable);
        model.addAttribute("Member",member);
        model.addAttribute("Package",aPackage);
        model.addAttribute("remain",packageDate.getRemaincount());
        model.addAttribute("paging",paging);
        model.addAttribute("localDateTime", LocalDateTime.now());

        return "admin/admin_BookingUser";
    }

    //?????? ?????? ??????
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public String bookingCheckdfs(Model model, Principal principal, UserBookingForm userBookingForm,@PathVariable("id") Long id){

        UserBooking userBooking1 = userBookingService.getUserBooking(id);

//        PackageDate packageDate = adminPackageDateService

//        PackageDate packagedate = adminPackageDateService.getDate(id.intValue());
//        System.out.println("????????? : " + packagedate.getId());
//        Integer remaincount = packagedate.getRemaincount();

        model.addAttribute("userBooking",userBooking1);
//        model.addAttribute("remaincount",remaincount);
//        model.addAttribute("PackageDate",packagedate);

        return "admin/admin_BookingUser";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("package/bookingCheck")
    public @ResponseBody HashMap<String,String> bookingCheck1(@RequestParam ("packageNum") Long packageNum, @RequestParam("departure") String departure,
                                                              @RequestParam ("bookingNum") Long bookingNum, @RequestParam("bookingcount") Integer bookingcount){

        PackageDate packageDate = adminPackageDateService.getPackageDate(packageNum, departure);
        int remainCount = packageDate.getRemaincount();

        int a = adminPackageDateService.getBeforePay(packageNum,departure);


        String msg;
//        String msg2;
        System.out.println("????????????"+remainCount);

        UserBooking userBooking= userBookingService.getUserBooking(bookingNum);

        if(remainCount-a>=bookingcount) {
            //??????
            userBookingService.modifyBookingStatus(userBooking,1);

            msg ="none";
//            System.out.println(msg);



        }else {
            userBookingService.modifyBookingStatus(userBooking,3);
            msg = "??????";

            System.out.println(msg);



        }

        HashMap<String,String> info = new HashMap<String,String>();

        info.put("msg",msg);


        System.out.println("?????????" + info.size());


        return info;

    }

    //????????? ?????? ??????
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/packageForm")
    public String createPackage(Model model) {
        model.addAttribute("packageCreate",new PackageCreate());
        return "admin/admin_Package";
    }

    @PostMapping("/packageForm")
    public String createPackagePost(@Valid PackageCreate packageCreate, BindingResult bindingResult,@RequestParam(name = "image1",required = false) MultipartFile multipartFile1,
                                    @RequestParam(name = "image2",required = false) MultipartFile multipartFile2) throws IOException, ParseException {

        String fileName1 = StringUtils.cleanPath(multipartFile1.getOriginalFilename());
        String fileName2 = StringUtils.cleanPath(multipartFile2.getOriginalFilename());


        if(bindingResult.hasErrors()){
            //????????? ???????????? ????????? ??????
            log.info(bindingResult.toString());
            return "admin/admin_Package";
        }

        Package aPackage = adminPackageService.create(packageCreate,fileName1,fileName2);
        String uploadDir1 =  "package-preview/" + aPackage.getId();
        String uploadDir2 =  "package-detail/" + aPackage.getId();
        PackageFileUpload.saveFile1(uploadDir1,fileName1,multipartFile1);
        PackageFileUpload.saveFile2(uploadDir2,fileName2,multipartFile2);

        List<PackageDate > packageDate = adminPackageDateService.createDate(packageCreate,aPackage);


        return "redirect:/admin/packageList";
    }


    //????????? ?????????
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/packageList")
    public String packageList(Model model, @PageableDefault Pageable pageable) {

        Page<Package> paging = adminPackageService.getList(pageable);
        model.addAttribute("paging",paging);

        return "admin/admin_PackageList";
    }

    //????????? ?????? ??????
    @PostMapping("/package/delete")
    public String packageDelete(@RequestParam List<String> packageNum) {

        for(int i=0; i<packageNum.size(); i++){
            Long id = Long.valueOf(packageNum.get(i));
            adminPackageService.deletePackage(id);
        }
        return "redirect:/admin/packageList";
    }

    //????????? ????????? ??????
    @PostMapping("/booking/departure/delete")
    public String departureDelete(@RequestParam List<String> packageDate) {

        for(int i=0; i<packageDate.size(); i++){
            Integer id = Integer.valueOf(packageDate.get(i));
            adminPackageDateService.deleteDate(id);
        }
        return "redirect:/admin/packageList";
    }


    //????????? ?????? ??????

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/package/modify/{id}")
    public String packageModify(PackageCreate packageModify,
                                BindingResult bindingResult,@PathVariable("id") Long id ){

        Package aPackage = adminPackageService.getPackage(id);

        packageModify.setPackageName(aPackage.getPackageName());
        packageModify.setLocation1(aPackage.getLocation1());
        packageModify.setLocation2(aPackage.getLocation2());
        packageModify.setHotelName(aPackage.getHotelName());
        packageModify.setTransport(aPackage.getTransport());
        packageModify.setPackageInfo((aPackage.getPackageInfo()));
        packageModify.setCount(aPackage.getCount());
        packageModify.setPostStart(aPackage.getPostStart());
        packageModify.setPostEnd(aPackage.getPostEnd());
        packageModify.setTravelPeriod(aPackage.getTravelPeriod());
        packageModify.setKeyword(aPackage.getKeyword());

        return "admin/admin_PackageModify";
    }

    @PostMapping("/package/modify/{id}")
    public String packageModify2(@Valid PackageCreate packageCreate,BindingResult bindingResult,
                                 @RequestParam("image1") MultipartFile multipartFile1,
                                 @RequestParam("image2") MultipartFile multipartFile2,

                                 @PathVariable("id") Long id) throws IOException {

        Package aPackage = adminPackageService.getPackage(id);

        String preview = StringUtils.cleanPath(multipartFile1.getOriginalFilename());
        String detail = StringUtils.cleanPath(multipartFile2.getOriginalFilename());

        adminPackageService.modify(aPackage,packageCreate,preview,detail);

        String uploadDir1 = "package-preview/" + aPackage.getId();
        String uploadDir2 = "package-detail/" + aPackage.getId();

        FileUploadUtil.saveFile(uploadDir1,preview,multipartFile1);
        FileUploadUtil.saveFile(uploadDir2,preview,multipartFile2);

        return "redirect:/admin/packageList";

    }




    // ?????????  ????????? ??????
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/package/departure/modify/{id}")
    public String departureModify(PackageCreate packageModify,
                                  BindingResult bindingResult,@PathVariable("id") Integer id ){

        PackageDate packageDate = adminPackageDateService.getDate(id);

        packageModify.setAprice(packageDate.getAprice());
        packageModify.setBprice(packageDate.getBprice());
        packageModify.setCprice(packageDate.getCprice());
        packageModify.setDiscount(packageDate.getDiscount());
        packageModify.setDeparture(packageDate.getDeparture());

        return "admin/admin_PackageDateModify";
    }

    @PostMapping("/package/departure/modify/{id}")
    public String departureModify2(@Valid PackageCreate packageCreate,BindingResult bindingResult,
                                   @PathVariable("id") Integer id) throws IOException {


        PackageDate packageDate = adminPackageDateService.getDate(id);

        adminPackageDateService.modifyDate(packageDate,packageCreate);

        return "redirect:/admin/packageList";

    }




//?????? ??????

    //?????? ?????????
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/user")
    public String userList(Model model, @PageableDefault Pageable pageable, Member member) {

        Page<Member> paging = memberService.getList(pageable);
        model.addAttribute("Member",member);
        model.addAttribute("paging",paging);

        return "admin/admin_UserList";
    }

    //?????? ?????? ??????(????????????)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/user/update/{email}")
    public String updateUser(Model model, Principal principal,
                             MemberCreate memberCreate,@PathVariable("email") String email){

        Member member = memberService.getMember(String.valueOf(email));
        memberCreate.setBirth(member.getBirth());
        memberCreate.setEmail(member.getEmail());
        memberCreate.setName(member.getName());
        memberCreate.setPhone_num(member.getPhone());
        memberCreate.setPassword1(member.getPassword());
        memberCreate.setPoint(member.getPoint());

        String keywords = member.getKeyword();
        if(keywords!=null) {
            System.out.println(keywords);
            model.addAttribute("keywords",keywords);
        }else{
            model.addAttribute("keywords","n");

        }
        System.out.println(keywords);

        model.addAttribute("social",member.getSocial());

        return "admin/admin_profileUpdate";

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/user/update/{email}")
    public String updateUser(@Valid MemberCreate memberCreate, BindingResult bindingResult,@PathVariable("email") String eamil){

        Member member = memberService.getMember(String.valueOf(eamil));

        String keyword = memberCreate.getKeyword();
        System.out.println(keyword);


        adminPackageService.updateUser(member,memberCreate.getName(),memberCreate.getBirth(), memberCreate.getKeyword(),memberCreate.getPhone_num(), memberCreate.getPoint());

        return "redirect:/admin/user";
    }

    //?????? ?????? (?????????)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/user/delete/{email}")
    public String packageDelete(@PathVariable("email") String email) {
        Member member = memberService.getMember(String.valueOf(email));
        adminPackageService.memberDelete(member);
        return "redirect:/admin/user";
    }



//????????????

    //????????? ????????? ?????? ??????
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/sales/package")
    public String salesPackage(Model model,@PageableDefault Pageable pageable, Pay pay, Package aPackage) {

        Page<Pay> paging =adminSalesService.getPayList(pageable);
        model.addAttribute("paging",paging);
        model.addAttribute("Pay",pay);
        model.addAttribute("Package",aPackage);

        return "admin/admin_SalesPackage";
    }

    //?????? ????????????
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/sales/package/download")
    public ResponseEntity pay(HttpServletResponse response, boolean excelDownload){
        return ResponseEntity.ok(salesExcelService.getPackageSales(response,excelDownload));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("admin_salespackagelist")
    public String admin_salespackagelist() {
        return "admin/admin_SalesPackageList";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/sales/user")
    public String salesUser(Model model,@PageableDefault Pageable pageable, Pay pay, Package aPackage, Member member) {

        List<Pay> paylist = payService.findAll();

        Iterator<Pay> it = paylist.iterator();

        List<Long> memberid = new ArrayList<>();

        while (it.hasNext()){
            memberid.add(it.next().getMember().getId());
        }

        Page<Member> paging = memberService.getpayList(memberid, pageable);

        model.addAttribute("paging",paging);
        model.addAttribute("Pay",pay);
        model.addAttribute("Package",aPackage);

        return "admin/admin_salesUser";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("admin_salesuserlist")
    public String admin_salesuserlist() {
        return "admin/admin_SalesUserList";
    }
}