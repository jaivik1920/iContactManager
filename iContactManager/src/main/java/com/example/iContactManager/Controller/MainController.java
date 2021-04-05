package com.example.iContactManager.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Random;

import javax.servlet.http.HttpSession;
import javax.xml.crypto.Data;

import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.iContactManager.EmailService.EmailService;
import com.example.iContactManager.Entity.User;
import com.example.iContactManager.Message.Message;
import com.example.iContactManager.Repository.UserRepo;


@Controller
public class MainController {
	
	@Autowired
	private UserRepo userrepo;
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	
	@Autowired
	private BCryptPasswordEncoder passwordencoder;
	
	@Autowired
	private EmailService emailservice;
	
	
	@GetMapping("/iContactManager/")
	public String home(Model model) {
		model.addAttribute("title","iContactManager");
		return "home";
	}
	
	@GetMapping("/iContactManager/login")
	public String login(Model model) {
		model.addAttribute("title","Login-iContactManager");
		return "login";
	}
	
	@GetMapping("/iContactManager/signup")
	public String signup(Model model) {
		model.addAttribute("title", "Sigup-iContactManager");
		return "signup";
	}
	
	@PostMapping("/iContactManager/signuphandler")
	public String signupHandler(@ModelAttribute User user,Model model,HttpSession session) {
				
		model.addAttribute("user", user);
		model.addAttribute("title", "Sigup-iContactManager");
		
		System.out.println(user);
		
		Random random=new Random();
		
		String number=String.format("%06d",random.nextInt(1000000));
		
		String encodeotp=passwordencoder.encode(number);
		
		session.setAttribute("encodeotp", encodeotp); 
		
		System.out.println(number);
		number="Your otp for singup  is <strong>"+number+"</strong>";
		
		boolean status=this.emailservice.sendEmail("iContactManager Registration", number, user.getEmail());
		
		

		
		if(status) {
			return "verifyuser";
		}
		else {
			session.setAttribute("message", new Message("Something went wrong!!!", "alert-danger"));
			return "redirect:signup";
		}
		
		
		
		
	}
	
	@PostMapping("/iContactManager/verifyuser")
	public String verifyUser(@ModelAttribute User user,@RequestParam("otp") String otp,HttpSession session) {
		try {
		
		String encodeotp=(String) session.getAttribute("encodeotp");
  
        
		if(this.passwordencoder.matches(otp, encodeotp)) {
			user.setImageString("default.jpg");
			user.setRoleString("ROLE_USER");
			user.setPassword(encoder.encode(user.getPassword()));			
			this.userrepo.save(user);
			session.setAttribute("message", new Message("Registerd Successfully!!!", "alert-success"));
			boolean status=this.emailservice.sendEmail("iContactManager Welcome","Thank you for joinig us.<br>Now start saving your contact on cloud.<br><br><br> cheers <strong>iContactManager!!!</strong>", user.getEmail());
			return "redirect:login";
		}
		else {
		session.setAttribute("message", new Message("Otp Wrong !!!", "alert-danger"));
		}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return "redirect:signup";
		
	}
	
	
	

	@GetMapping("/iContactManager/forgotpassword")
	public String forgotpassword() {
		return "forgotpassword";
	}
	
	
	
	@PostMapping("/iContactManager/forgotpasswordhandler")
	public String forgotpasswordhandler(@RequestParam("username") String username,Model model,HttpSession session) {
		
		
		
		
		Random random=new Random();
		
		String number=String.format("%06d",random.nextInt(1000000));
		
		System.out.println(number);
		number="Your otp is <strong>"+number+"</strong>";
		
		boolean status=this.emailservice.sendEmail("Otp-verification", number, username);
		
		String encodeotp=passwordencoder.encode(number);				
		session.setAttribute("encodeotp", encodeotp);
		session.setAttribute("username", username);
		

		
		if(status) {
			session.setAttribute("message", new Message("Otp send Successfully!!!", "alert-success"));
		}
		else {
			session.setAttribute("message", new Message("Something went wrong!!!", "alert-danger"));
			return "forgotpassword";
		}
		
		return "otpverification";
	}
	
	@PostMapping("/iContactManager/otphandler")
	public String otphandler(@RequestParam("otp") String otp,Model model,HttpSession session) {
			
		String encodeotp=(String) session.getAttribute("encodeotp");
		
		String username=(String) session.getAttribute("username");
		
		System.out.println("urlotp="+encodeotp); 
		
		if(this.passwordencoder.matches(otp, encodeotp)) {
			return "newpassword";
		}
		session.setAttribute("message", new Message("Otp Wrong !!!", "alert-danger"));
		return "otpverification";
	}
	
	@GetMapping("/iContactManager/newpassword")
	
	public String newpassword(Model model) {
		
		return "newpassword";
	}
	
	@PostMapping("/iContactManager/newpasswordhandler")
	
	public String newpasswordhandler(@RequestParam("newpassword") String newpassword,Model model,HttpSession session) {
		
		String username=(String) session.getAttribute("username");
		User user=userrepo.getUserByUserName(username);
		
		user.setPassword(this.passwordencoder.encode(newpassword));
		userrepo.save(user);
		session.setAttribute("message", new Message("password changed Successfully!!!", "alert-success"));
		return "login";
	}
}
