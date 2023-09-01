package com.garganttua.server.modules.spring;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@CrossOrigin(origins = "*")
@RequestMapping("/test2")
public class ControllerTest {
	
	@RequestMapping(value = "", method = RequestMethod.GET)
    private ResponseEntity<?> getEntity(){
		return new ResponseEntity<>("test", HttpStatus.OK);
	}

}
