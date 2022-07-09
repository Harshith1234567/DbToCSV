package com.javatechie.crud.example.cofig;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.RowMapper;

import com.javatechie.crud.example.entity.Product;
import com.javatechie.crud.example.processor.ProProcessor;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
	
	@Autowired
	  private JobBuilderFactory jobBuilderFactory;
	   
	  @Autowired
	  private StepBuilderFactory stepBuilderFactory;
	  
	  @Autowired
	  private DataSource dataSource;
	  
	  @Bean
	  public JdbcCursorItemReader<Product> reader(){
		  JdbcCursorItemReader<Product> reader=new JdbcCursorItemReader<Product>();
		  reader.setDataSource(dataSource);
		  reader.setSql("select id, name,quantity , price from javatechie.product");
		  reader.setRowMapper(new RowMapper<Product>() {
			
			@Override
			public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
				Product p=new Product();
				p.setId(rs.getInt("id"));
				p.setName(rs.getString("name"));
				p.setQuantity(rs.getInt("quantity"));
				p.setPrice(rs.getInt("price"));
				
				return p;
			}
		}
				  );
		  return reader;
	  }

	  @Bean
	  public FlatFileItemWriter<Product> writer(){
		  FlatFileItemWriter<Product> writer=new FlatFileItemWriter<Product>();
		  writer.setResource(new FileSystemResource("C://Users/DELL/Documents/output_csv.csv"));
//		  writer.setResource(new FileSystemResource("C:/Users/DELL/Documents/output_csv.csv"));
//		  writer.setResource(new FileSystemResource("C:\Users\DEL\Documents\output_csv.csv"));
//		  writer.setResource(new FileSystemResource("/cygdrive/c/Users/DELL/Documents/output_csv.csv"));
//		  writer.setResource(new FileSystemResource("/cygdrive/C/Users/DELL/Documents/output_csv.csv"));

//		  writer.setResource(new ClassPathResource("products.csv"));
		  DelimitedLineAggregator<Product> aggregator=new DelimitedLineAggregator<>();
		  BeanWrapperFieldExtractor<Product> fieldExtractor=new BeanWrapperFieldExtractor<>();
		  fieldExtractor.setNames(new String[] {"id","name","quantity","price"});
		  aggregator.setFieldExtractor(fieldExtractor);
		  writer.setLineAggregator(aggregator);
		  System.out.println("1");
		  return writer;
		  
	  }
	  
	  @Bean
	  public Step executeStep() {
		  System.out.println("2");
		  return stepBuilderFactory.get("executeStep").<Product,Product>chunk(10).reader(reader()).processor(new ProProcessor()).writer(writer()).build();
		  
	  }
	   
	  @Bean
	  public Job processJob() {
		  return jobBuilderFactory.get("processJob").incrementer(new RunIdIncrementer()).flow(executeStep()).end().build();
	  }
	  
//	  @Bean
//	  public Job exportUserJob() {
//	   return jobBuilderFactory.get("processJob")
//	     .incrementer(new RunIdIncrementer())
//	     .flow(executeStep())
//	     .end()
//	     .build();
//	  }
}
