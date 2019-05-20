package com.fcs.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

@Controller
public class CategoriesController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CategoriesController.class);
	
	@RequestMapping(value = "/c", method = RequestMethod.GET)
	public String listCategory(Model model) throws InterruptedException, ExecutionException {
		Firestore db = FirestoreOptions.getDefaultInstance().getService();
		ApiFuture<QuerySnapshot> query = db.collection("categoriedu").get();
		List<QueryDocumentSnapshot> documents = query.get().getDocuments();
		List<HashMap<String, String>> listId = new ArrayList<>();
		for (QueryDocumentSnapshot document : documents) {
			  HashMap<String, String> hashMap = new HashMap<>();
			  Category category = document.toObject(Category.class);
			  hashMap.put("id", document.getId());
			  hashMap.put("urlimg",category.getUrlimg());
			  hashMap.put("totalCourse", category.getTotalCourse());
			  listId.add(hashMap);
			}
		List<Category> categories = query.get().toObjects(Category.class);
		model.addAttribute("listId",listId );
		LOGGER.info("OBJECT :" + categories);
		return "views/admin/category/list";
	}
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String formAdd() {
		
		return "views/admin/category/create";
	}
	@RequestMapping(value ="/add", method = RequestMethod.POST)
	public void addCategory(Model model, String urlimg, String id,HttpServletResponse serp) throws IOException {
		Firestore db =FirestoreOptions.getDefaultInstance().getService();
		Category category = null;
		Map<String, Object> categories = new HashMap<>();
		categories.put("urlimg", urlimg);
		categories.put("id",id);
		ApiFuture<WriteResult> document = db.collection("categoriedu").document(id).set(categories);
		serp.sendRedirect("/c");
	}
	@GetMapping(value = "/c/{id}")
	public String editForm(Model model, @PathVariable("id") String id) throws InterruptedException, ExecutionException {
		Firestore db = FirestoreOptions.getDefaultInstance().getService();
		ApiFuture<QuerySnapshot> future =
			    db.collection("categoriedu").get();
		List<QueryDocumentSnapshot> documents = future.get().getDocuments();
		List<HashMap<String, String>> listId = new ArrayList<>();
		for (QueryDocumentSnapshot queryDocumentSnapshot : documents) {
			
			LOGGER.info(queryDocumentSnapshot.getId());
			
			if(queryDocumentSnapshot.getId().equals(id)) {
				HashMap<String, String> hashMap = new HashMap<>();
				  Category category = queryDocumentSnapshot.toObject(Category.class);
				  
				  hashMap.put("id", queryDocumentSnapshot.getId());
				  hashMap.put("urlimg",category.getUrlimg());
				  hashMap.put("totalCourse", category.getTotalCourse());
				  listId.add(hashMap);
			}
		}
		List<Category> categories = future.get().toObjects(Category.class);
		model.addAttribute("listId",listId );
		LOGGER.info("==534545" + listId);
		return "views/admin/category/edit";
	}
	@RequestMapping(value = "/c/{id}",method = RequestMethod.POST)
	public void updateCategory(Model model, String id, String urlimg, HttpServletResponse resp) throws IOException {
		Firestore db =FirestoreOptions.getDefaultInstance().getService();
		DocumentReference docRef = db.collection("categoriedu").document(id);
		Category category = null;
		Map<String, Object> categories = new HashMap<>();
		categories.put("urlimg", urlimg);
		categories.put("id",id);
		ApiFuture<WriteResult> document = docRef.update(categories);
		LOGGER.info("New =====" +categories );
		resp.sendRedirect("/c");
	}
	
}
