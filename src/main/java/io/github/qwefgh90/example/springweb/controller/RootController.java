package io.github.qwefgh90.example.springweb.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

	private final static Logger LOG = LoggerFactory.getLogger(RootController.class);

	@RequestMapping(value = "/plus", method = RequestMethod.GET)
	public ResponseEntity<String> plus(@RequestParam int n1, @RequestParam int n2) {
		Map<String, String> map = new HashMap<>();
		JSONObject obj = new JSONObject();
		obj.put("result", String.valueOf(n1 + n2));
		return new ResponseEntity<String>(String.valueOf(n1 + n2), HttpStatus.OK);
	}
	@RequestMapping(value = "/minus", method = RequestMethod.GET)
	public ResponseEntity<String> minus(@RequestParam int n1, @RequestParam int n2) {
		Map<String, String> map = new HashMap<>();
		JSONObject obj = new JSONObject();
		obj.put("result", String.valueOf(n1 - n2));
		return new ResponseEntity<String>(String.valueOf(n1 - n2), HttpStatus.OK);
	}
	@RequestMapping(value = "/times", method = RequestMethod.GET)
	public ResponseEntity<String> times(@RequestParam int n1, @RequestParam int n2) {
		Map<String, String> map = new HashMap<>();
		JSONObject obj = new JSONObject();
		obj.put("result", String.valueOf(n1 * n2));
		return new ResponseEntity<String>(String.valueOf(n1 * n2), HttpStatus.OK);
	}
	@RequestMapping(value = "/divide", method = RequestMethod.GET)
	public ResponseEntity<String> divide(@RequestParam int n1, @RequestParam int n2) {
		Map<String, String> map = new HashMap<>();
		JSONObject obj = new JSONObject();
		obj.put("result", n2 == 0 ? "Division by zero" : String.valueOf(n1 / n2));
		return new ResponseEntity<String>( n2 == 0 ? "Division by zero" : String.valueOf(n1 / n2), HttpStatus.OK);
	}
}
