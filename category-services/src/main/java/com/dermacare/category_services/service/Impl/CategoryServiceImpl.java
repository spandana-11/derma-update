package com.dermacare.category_services.service.Impl;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dermacare.category_services.dto.CategoryDto;
import com.dermacare.category_services.entity.Category;
import com.dermacare.category_services.entity.Services;
import com.dermacare.category_services.entity.SubServices;
import com.dermacare.category_services.repository.CategoryRepository;
import com.dermacare.category_services.repository.ServicesRepository;
import com.dermacare.category_services.repository.SubServiceRepository;
import com.dermacare.category_services.service.CategoryService;
import com.dermacare.category_services.service.ServicesService;
import com.dermacare.category_services.util.HelperForConversion;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepository repository;

	@Autowired
	private ServicesRepository serviceManagmentRepository;

	@Autowired
	private ServicesService service;
	
	@Autowired
	public SubServiceRepository subServiceRepository;
	@Autowired
	private SubServicesServiceImpl subService;

	public CategoryDto addCategory(CategoryDto dto) {
		Category category = HelperForConversion.toEntity(dto);
		Category savedCategory = repository.save(category);
		return HelperForConversion.toDto(savedCategory);
	}

	public boolean findByCategoryName(String categoryName) {
		Optional<Category> category = repository.findByCategoryName(categoryName);
		return category.isPresent();
	}

	public List<CategoryDto> findAllCategories() {
		List<Category> listOfCategories = repository.findAll();
		if (listOfCategories.isEmpty()) {
			return null;
		}
		return HelperForConversion.converToDtos(listOfCategories);
	}

	public CategoryDto getCategorById(String categoryId) {
		Category category = repository.findById(new ObjectId(categoryId)).orElseThrow(
				()->new RuntimeException("Category Not found With : "+categoryId));
		return HelperForConversion.toDto(category);
	}

	public CategoryDto updateCategoryById(ObjectId categoryId, CategoryDto updateDto) {
	    Category existing = repository.findById(categoryId)
	        .orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));

	    Optional<Category> optional = repository.findByCategoryName(updateDto.getCategoryName());
	    if (optional.isPresent()) {
	        Category duplicate = optional.get();
	        if (!duplicate.getCategoryId().equals(categoryId)) {
	            throw new RuntimeException("Duplicate category name found: " + updateDto.getCategoryName());
	        }
	    }

	    String oldCategoryName = existing.getCategoryName();

	    if (updateDto.getCategoryImage() != null) {
	        byte[] categoryImageBytes = Base64.getDecoder().decode(updateDto.getCategoryImage());
	        existing.setCategoryImage(categoryImageBytes);
	    }

	    existing.setCategoryName(updateDto.getCategoryName());
	    existing.setDescription(updateDto.getDescription());

	    Category savedCategory = repository.save(existing);

	    // Update category name in Services
	    List<Services> services = serviceManagmentRepository.findByCategoryName(oldCategoryName);
	    System.out.println("Found Services: " + services.size());
	    for (Services service : services) {
	        service.setCategoryName(updateDto.getCategoryName());
	    }
	    serviceManagmentRepository.saveAll(services);

	    // ✅ Update category name in SubServices
	    List<SubServices> subServices = subServiceRepository.findByCategoryName(oldCategoryName);
	    System.out.println("Found SubServices: " + subServices.size());
	    for (SubServices subService : subServices) {
	        subService.setCategoryName(updateDto.getCategoryName());
	    }
	    subServiceRepository.saveAll(subServices);

	    return HelperForConversion.toDto(savedCategory);
	}



	@Override
	public boolean findByCategoryId(String categoryId) {
		Optional<Category> optional = repository.findById(new ObjectId());
		return optional.isPresent();
	}

	@Override
	public void deleteById(String categoryId) {
		// TODO Auto-generated method stub
		
	}

	public void deleteCategoryById(ObjectId categoryId) {
	    // Step 1: Fetch the existing category
	    Category existingCategory = repository.findById(categoryId)
	        .orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));

	    // Step 2: Fetch and delete related services
	    List<Services> services = serviceManagmentRepository.findByCategoryName(existingCategory.getCategoryName());
	    if (!services.isEmpty()) {
	        System.out.println("Found Services: " + services.size());
	        // Delete all services associated with this category
	        serviceManagmentRepository.deleteAll(services);
	    }

	    // Step 3: Fetch and delete related subservices
	    List<SubServices> subServices = subServiceRepository.findByCategoryName(existingCategory.getCategoryName());
	    if (!subServices.isEmpty()) {
	        System.out.println("Found SubServices: " + subServices.size());
	        // Delete all subservices associated with this category
	        subServiceRepository.deleteAll(subServices);
	    }

	    // Step 4: Finally, delete the category itself
	    repository.deleteById(categoryId);
	}

}
