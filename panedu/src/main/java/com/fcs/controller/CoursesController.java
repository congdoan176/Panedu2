package com.fcs.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.google.cloud.firestore.WriteResult;

import io.opencensus.internal.PublicForTesting;

@Controller
public class CoursesController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CoursesController.class);
	
	
	@GetMapping(value = "/cou")
	public String listCourse(Model model) throws InterruptedException, ExecutionException {
		Firestore db = FirestoreOptions.getDefaultInstance().getService();
		ApiFuture<QuerySnapshot> queryCourse = db.collection("categoriedu").get();
		List<QueryDocumentSnapshot> documents = queryCourse.get().getDocuments();
		List<String> listId = new ArrayList<>();
		List<HashMap<String, String>> listCourse = new ArrayList<>();
		for (QueryDocumentSnapshot document : documents) {
			  listId.add(document.getId());
			}
		for (String id : listId) {
			  String idDoc = id;
				ApiFuture<QuerySnapshot> query1 = db.collection("categoriedu/" + id + "/coursedu").get();
				List<QueryDocumentSnapshot> document2 = query1.get().getDocuments();
		
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
				  LOGGER.info("List  : " + listCourse);
			}
		model.addAttribute("listCourse",listCourse );
		model.addAttribute("listId",listId );
		LOGGER.info("Course : " + listId);
		return "views/admin/course/list";
	}
	@GetMapping(value = "/addcou")
	private String formCourse(Model model) throws InterruptedException, ExecutionException {
		Firestore db = FirestoreOptions.getDefaultInstance().getService();
		ApiFuture<QuerySnapshot> query = db.collection("categoriedu").get();
		List<QueryDocumentSnapshot> documents = query.get().getDocuments();
		List<HashMap<String, String>> listId = new ArrayList<>();
		for (QueryDocumentSnapshot document : documents) {
			  HashMap<String, String> hashMap = new HashMap<>();
			  Category category = document.toObject(Category.class);
			  hashMap.put("id", document.getId());
			  listId.add(hashMap);
			}
		model.addAttribute("listId", listId);
		LOGGER.info("Load "+ listId);
		return "views/admin/course/create";
	}
	@RequestMapping(value="/addcou", method = RequestMethod.POST)
	public void addCourse(Model model,
			 String teacher, String id, String money, String urlimg,String description,String idColle,  HttpServletResponse resp) 
			throws InterruptedException, ExecutionException, IOException {
		Firestore db = FirestoreOptions.getDefaultInstance().getService();
			 Map<String, Object> course = new HashMap<>(); 
			  course.put("teacher",teacher); 
			  course.put("money", teacher); 
			  course.put("urlimg", urlimg);
			  course.put("description", description); 
			  course.put("id", id);
			  course.put("idColle", idColle);
		  ApiFuture<WriteResult> addDocument =db.collection("categoriedu/" + idColle + "/coursedu").document(id).set(course);
		resp.sendRedirect("/cou");
	}
	@GetMapping(value = "/cou/{id}")
	public String insertForm(Model model, @PathVariable("id")String id) throws InterruptedException, ExecutionException {
		Firestore db = FirestoreOptions.getDefaultInstance().getService();
		ApiFuture<QuerySnapshot> query = db.collection("categoriedu").get();
		List<QueryDocumentSnapshot> documents = query.get().getDocuments();
		List<HashMap<String, String>> listId = new ArrayList<>();
		for (QueryDocumentSnapshot document : documents) {
			  HashMap<String, String> hashMap = new HashMap<>();
			  Category category = document.toObject(Category.class);
			  hashMap.put("iddoc", document.getId());
			  listId.add(hashMap);
			}
		for (HashMap<String, String> idCollec : listId) {
			Collection<String> idColection = idCollec.values();
			ApiFuture<QuerySnapshot> query1 = db.collection("categoriedu/" + idColection + "/coursedu").get();
			List<QueryDocumentSnapshot> document2 = query1.get().getDocuments();
			
			List<HashMap<String, String>> course1 = new  ArrayList<>();
			for (QueryDocumentSnapshot document3 : document2) {
				if (document3.getId().equals(id)) {
					HashMap<String, String> hashMap3 = new HashMap<>();
					  Course course = document3.toObject(Course.class);
					  hashMap3.put("id", document3.getId());
					  hashMap3.put("urlimg", course.getUrlimg());
					  hashMap3.put("teacher", course.getTeacher());
					  hashMap3.put("money", course.getMoney());
					  hashMap3.put("description", course.getDescription());
					  course1.add(hashMap3);
				}
				LOGGER.info("=======" + course1);
			}
		}
		model.addAttribute("listId", listId);
		return "views/admin/course/edit";
	}
	@RequestMapping(value = "/cou/{id}",method = RequestMethod.POST)
	public String updateCourse() {
		
		return null;
	}
}
