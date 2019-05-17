package com.fcs.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fcs.entity.Category;
import com.fcs.entity.Course;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import io.opencensus.internal.PublicForTesting;

@Controller
public class CoursesController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CoursesController.class);
	
	
	@GetMapping(value = "/cou")
	public String listCourse(Model model) throws InterruptedException, ExecutionException {
		Firestore db = FirestoreOptions.getDefaultInstance().getService();
		ApiFuture<QuerySnapshot> queryCourse = db.collection("categoriedu").get();
		List<QueryDocumentSnapshot> documents = queryCourse.get().getDocuments();
		List<HashMap<String, String>> listId = new ArrayList<>();
		for (QueryDocumentSnapshot document : documents) {
			  HashMap<String, String> hashMap = new HashMap<>();
			  hashMap.put("id", document.getId());
			  listId.add(hashMap);
			  for (HashMap<String, String> idDocument : listId) {
				  String id = idDocument.get(listId);
					ApiFuture<QuerySnapshot> query1 = db.collection("categoriedu/" + id + "/coursedu").get();
					List<QueryDocumentSnapshot> document2 = query1.get().getDocuments();
					List<HashMap<String, String>> listCourse = new ArrayList<>();
					
					  for(DocumentSnapshot document3 : document2) {
						  
						  HashMap<String, String> hashMap3 = new HashMap<>();
						  
						  Course course = document3.toObject(Course.class);
						  
						  hashMap3.put("id", document3.getId());
						  hashMap3.put("urlimg", course.getUrlimg());
						  hashMap3.put("teacher", course.getTeacher());
						  hashMap3.put("money", course.getMoney());
						  hashMap3.put("description", course.getDescription());
						  listCourse.add(hashMap3);
					  }
					  model.addAttribute("listCourse",listCourse );
					  LOGGER.info("=========: " + idDocument.get(listId));
				}
				
			}
		model.addAttribute("listId",listId );
		LOGGER.info("Course : " + listId);
		return "views/admin/course/list";
	}
	@GetMapping(value = "/addcou")
	public String formCourse() {
		return "views/admin/course/create";
	}
	@RequestMapping(value="/addcou", method = RequestMethod.POST)
	public String addCourse() {
		return null;
	}
}
