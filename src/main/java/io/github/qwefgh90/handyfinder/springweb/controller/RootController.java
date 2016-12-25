package io.github.qwefgh90.handyfinder.springweb.controller;

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

import io.github.qwefgh90.handyfinder.springweb.model.DocumentDto;

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
/*
	@RequestMapping(value = "/directories", method = RequestMethod.GET)
	public ResponseEntity<List<Directory>> getDirectories() {
		try {
			return new ResponseEntity<List<Directory>>(rootService.getDirectories(), HttpStatus.OK);
		} catch (SQLException e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
			return new ResponseEntity<List<Directory>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/directories", method = RequestMethod.POST)
	public ResponseEntity<String> updateDirectories(@RequestBody ArrayList<Directory> list) {
		try {
			rootService.updateDirectories(list);
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (SQLException e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/documents", method = RequestMethod.GET)
	public ResponseEntity<List<String>> search() {
		List<String> result;
		try {
			result = rootService.getTempPathForAllDocumentList();
			return new ResponseEntity<List<String>>(result, HttpStatus.OK);
		} catch (IOException e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
			return new ResponseEntity<List<String>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}	

	@RequestMapping(value = "/documents", method = RequestMethod.GET, params="keyword")
	public ResponseEntity<List<DocumentDto>> search(@RequestParam String keyword) {
		Optional<List<DocumentDto>> result = Optional.empty();
		result = rootService.search(keyword);
		if (result.isPresent()) {
			return new ResponseEntity<List<DocumentDto>>(result.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<List<DocumentDto>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/documents/count", method = RequestMethod.GET)
	public ResponseEntity<Integer> getDocumentCount() {
		int count = rootService.getDocumentCount();
		return new ResponseEntity<Integer>(count, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/document/content", method = RequestMethod.GET)
	public ResponseEntity<String> search(@RequestParam String pathString, @RequestParam String keyword){
		Optional<String> contentResult = rootService.search(keyword, pathString);
		if(contentResult.isPresent()){
			return new ResponseEntity<String>(contentResult.get(), HttpStatus.OK);
		}else{
			return new ResponseEntity<String>("can't load content on your disk", HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/supportType", method = RequestMethod.POST)
	public ResponseEntity<String> updateIndexType(@RequestBody SupportTypeDto supportType) {
		try {
			rootService.updateSupportType(supportType);
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (IOException e) {
			LOG.warn(ExceptionUtils.getStackTrace(e));
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/supportTypes", method = RequestMethod.GET)
	public ResponseEntity<List<SupportTypeDto>> getSupportTypes() {
		return new ResponseEntity<List<SupportTypeDto>>(rootService.getSupportType(), HttpStatus.OK);
	}

	@RequestMapping(value = "/supportTypes", method = RequestMethod.POST)
	public ResponseEntity<String> updateIndexType(@RequestBody List<SupportTypeDto> supportTypeList) {
		try {
			rootService.updateSupportsType(supportTypeList);
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (IOException e) {
			LOG.warn(ExceptionUtils.getStackTrace(e));
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/version", method = RequestMethod.GET)
	public ResponseEntity<Map<String,String>> getVersion() {
		return new ResponseEntity<Map<String,String>>(rootService.getVersion(), HttpStatus.OK);
	}

	@RequestMapping(value = "/onlineVersion", method = RequestMethod.GET)
	public ResponseEntity<Map<String,String>> getOnlineVersion() {
		return new ResponseEntity<Map<String,String>>(rootService.getOnlineVersion(), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/options", method = RequestMethod.GET)
	public ResponseEntity<OptionDto> getOption() {
		return new ResponseEntity<OptionDto>(rootService.getOption(), HttpStatus.OK);
	}

	@RequestMapping(value = "/options", method = RequestMethod.POST)
	public ResponseEntity<String> setOption(@RequestBody OptionDto optionDto) {
		rootService.setOption(optionDto);
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
 	*/
}
