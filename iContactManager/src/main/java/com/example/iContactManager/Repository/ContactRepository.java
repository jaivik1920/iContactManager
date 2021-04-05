package com.example.iContactManager.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.iContactManager.Entity.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
	
	@Query("from Contact u where u.user.id =:userid")
	public Page<Contact> findContactsByUser(@Param("userid") int userid,Pageable pageable);
	
	@Query("from Contact u where u.user.id =:userid")
	public List<Contact> findContactsByUser(@Param("userid") int userid);
	
	

}
