package com.javatechie.crud.example.processor;

import org.springframework.batch.item.ItemProcessor;

import com.javatechie.crud.example.entity.Product;

public class ProProcessor implements ItemProcessor<Product,Product>{

	@Override
	public Product process(Product item) throws Exception {
		
		//final String name=item.getName().toUpperCase();
		Product p=new Product(item.getId(),item.getName(),item.getQuantity(),item.getPrice());
		return p;
	}
	
}
