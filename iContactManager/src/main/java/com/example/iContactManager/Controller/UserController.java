package com.example.iContactManager.Controller;

import java.net.http.HttpRequest;
import com.razorpay.*;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.swing.text.AbstractDocument.Content;

import org.apache.catalina.startup.HomesUserDatabase;
import org.aspectj.weaver.ast.Var;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.iContactManager.EmailService.EmailService;
import com.example.iContactManager.Entity.Contact;
import com.example.iContactManager.Entity.User;
import com.example.iContactManager.Message.Message;
import com.example.iContactManager.Repository.ContactRepository;
import com.example.iContactManager.Repository.UserRepo;
import com.sun.el.stream.Optional;

import javassist.expr.NewArray;

@Controller
@RequestMapping("iContactManager/user")
public class UserController {
	
	
	@Autowired 
	private UserRepo userrepo;
	
	@Autowired
	private ContactRepository contactrepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordencoder;

	
	
	@GetMapping("/index")
	public String home(Principal principal,Model model) {
		
		model.addAttribute("user", userrepo.getUserByUserName(principal.getName()));
		model.addAttribute("title", "Dashboard-iContactManager");
		
		return "user/index";
	}

	
	@GetMapping("/")
	public String user_home(Principal principal,Model model) {	
		System.out.println("hiii working");
		
		String usernameString=principal.getName();
		System.out.println(usernameString);
		
		User user=userrepo.getUserByUserName(usernameString);
		
		model.addAttribute("user", user);
		model.addAttribute("title", "Dashboard-iContactManager");
		System.out.println(user);
		
		return "user/index";
	}
	
	@GetMapping("/add_contact")
	public String add_contacts(Model model,Principal principal) {
		
		
		String usernameString=principal.getName();
		System.out.println(usernameString);
		
		User user=userrepo.getUserByUserName(usernameString);
		
		model.addAttribute("user", user);
		
		
		model.addAttribute("title", "Contact-iContactManager");
		
		return "user/add_contacts";
	}
	
	@PostMapping("/contact_handler")
	public String contact_handler(@ModelAttribute Contact contact ,Model model,Principal principal,HttpSession session) {
		
		String usernameString=principal.getName();
		System.out.println(usernameString);
		
		User user=userrepo.getUserByUserName(usernameString);
		
		
		contact.setImageString("default.jpg");
		
		contact.setUser(user);
		
		model.addAttribute("user", user);
		
		this.contactrepository.save(contact);
		
		session.setAttribute("message", new Message("Contact Added Successfully!!!","alert-success") );
		
		System.out.println(contact);
		
		return "redirect:add_contact";
	}
	
	
	@GetMapping("/view_contact/{page}")
	public String viewContacts(@PathVariable("page") int page,Principal principal,Model model) {
		
		
		String usernameString=principal.getName();
		System.out.println(usernameString);
		
		User user=userrepo.getUserByUserName(usernameString);
		
		model.addAttribute("user", user);
		
		Pageable pageable=PageRequest.of(page, 5);
		
		Page<Contact> contacts=this.contactrepository.findContactsByUser(user.getId(),pageable);
		
		System.out.println(contacts);
		
		model.addAttribute("title", "ViewContact-iContactManager");
		
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentpage", page);
		int totalpage=contacts.getTotalPages();
		if(totalpage==0)
			totalpage=1;
		model.addAttribute("totalpage", totalpage);
		System.out.println(totalpage);
		
		return "user/view_contact";
	}
	
	
	@GetMapping("/showcontact/{id}")
	public String showcontact(@PathVariable("id") int id,Model model,Principal principal) {
		
		
		model.addAttribute("user", userrepo.getUserByUserName(principal.getName()));
		
		try {
		java.util.Optional<Contact> contactoptional=this.contactrepository.findById(id);
		
		Contact contact=contactoptional.get();

		
		model.addAttribute("contact", contact);
		
		
		model.addAttribute("title", "Contact-iContactManager");
		
		
		if(contact.getUser().getId()==userrepo.getUserByUserName(principal.getName()).getId()) {
			System.out.println("hi");
			return "user/showcontact";
		}
		else {
			System.out.println("error");
		return "user/error403";
		}
		}
		catch (Exception e) {
			return "redirect:error";
		}
		
	}
	
	@GetMapping("/setting")
	public String setting(Principal principal,Model model) {
		
		model.addAttribute("user",userrepo.getUserByUserName(principal.getName()));
		model.addAttribute("title", "setting-iContactManager");
		return "user/setting";
	}
	
	
	@GetMapping("/changepassword")
	public String changepassword(Principal principal,Model model) {
		model.addAttribute("user",userrepo.getUserByUserName(principal.getName()));
		model.addAttribute("title", "changepassword");
		return "user/changepassword";
	}
	
	@PostMapping("/passwordhandler")
	public String passwordhandler(@RequestParam("oldpass") String oldpass, @RequestParam("newpass") String newpass,Principal principal,Model model,HttpSession session) {
		
		User user=userrepo.getUserByUserName(principal.getName());
		
		
		model.addAttribute("user",user);
		
		
		
		if(this.passwordencoder.matches(oldpass, user.getPassword())) {
			user.setPassword(this.passwordencoder.encode(newpass));
			userrepo.save(user);
			session.setAttribute("message", new Message("Password changed Successfully!!!","alert-success") );
			return "redirect:index";

		}
		else {
			session.setAttribute("message", new Message("Something went wrong!!! Your old password is wrong.","alert-danger") );
		}
		
		
		
		
		return "redirect:changepassword";
	}
	
	
	@GetMapping("/deletecontact/{id}")
	public String deleteContact(@PathVariable("id") Integer id,Principal principal,Model model) {
		User user=userrepo.getUserByUserName(principal.getName());
		List<Contact> contacts=this.contactrepository.findContactsByUser(user.getId());
		model.addAttribute("title", "ViewContact-iContactManager");
		model.addAttribute("user", user);
		model.addAttribute("contacts", contacts);
		
		java.util.Optional<Contact> contactoptional=contactrepository.findById(id);
		Contact contact=contactoptional.get();
		contactrepository.delete(contact);
		System.out.println(id);
		return "redirect:/iContactManager/user/view_contact/0";
	}
	
	@GetMapping("/updatecontact/{id}")
	public String updateContact(@PathVariable("id") Integer id,Principal principal,Model model) {
		try {
		User user=userrepo.getUserByUserName(principal.getName());
		model.addAttribute("user", user);		
		model.addAttribute("title", "Update-iContactManager");
		java.util.Optional<Contact> contactoptional=contactrepository.findById(id);
		Contact contact=contactoptional.get();
		model.addAttribute("contact", contact);
		if(contact.getUser().getId()==user.getId()) {
			System.out.println("ho");
			return "user/contact";
		}
		else {
			System.out.println("erro");
		return "user/error403";
		}
		}
		catch (Exception e) {
			return "redirect:error";
		}
		
	}
	
	
	@PostMapping("/contact_handler/{id}")
	public String updateContactHandler(@PathVariable("id") Integer id,Principal principal,Model model,@ModelAttribute Contact contact) {
		User user=userrepo.getUserByUserName(principal.getName());
		List<Contact> contacts=this.contactrepository.findContactsByUser(user.getId());
		model.addAttribute("title", "ViewContact-iContactManager");
		model.addAttribute("user", user);
		model.addAttribute("contacts", contacts);
		
		java.util.Optional<Contact> contactoptional=contactrepository.findById(id);
		Contact contact1=contactoptional.get();
		
		
	
			contact1.setEmailString(contact.getEmailString());
			contact1.setNameString(contact.getNameString());
			contact1.setPhone(contact.getPhone());
			
			contactrepository.save(contact1);
			return "redirect:/iContactManager/user/view_contact/0";

	}
	
	
	@GetMapping("/payment")
	public String makePayment(Principal principal,Model model) {
		
		model.addAttribute("user", userrepo.getUserByUserName(principal.getName()));
		model.addAttribute("title", "Payment");
		return "user/payment";
	}
	
	@PostMapping("/createpayment")
	@ResponseBody
	public String createPayment(Principal principal,Model model,@RequestBody Map<String, Object> data) throws RazorpayException {
		System.out.println("run");
		model.addAttribute("user", userrepo.getUserByUserName(principal.getName()));
		
		int amount=Integer.parseInt(data.get("amount").toString());
		
		RazorpayClient client=new RazorpayClient("rzp_test_qzfcNpMEANAbrK", "htxlZ7nOVujMcHDqnh338x0p");
		
		
		JSONObject options = new JSONObject();
		options.put("amount", amount*100);
		options.put("currency", "INR");
		options.put("receipt", "txn_123456");
		Order order = client.Orders.create(options);
		
		model.addAttribute("title", "Payment");
		
		return order.toString();
	}
	
	@GetMapping("/error")
	public String error(Model model) {
		model.addAttribute("title", "error");
		return "user/error403";
	}
	
	
	
}
