//package com.example.mariaDbForIcodic.DBO;
//
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import com.example.mariaDbForIcodic.model.EntityIcodic;
//import com.example.mariaDbForIcodic.repository.EntityRepository;
//
//@RestController
//@Component
//public class DBOperationRunner {
//
//     @Autowired
//     private DataReceiver dataReceiver;
//    
//     @Override
//     public void run(String... args) throws Exception {
// 
//         // Call the receiveAndInsertData method to add the new employee to the database
//         dataReceiver.receiveAndInsertData(newEmployee);
// 
//         System.out.println("----------New Employee Data saved into Database----------------------");
//     }
// 
//
//}