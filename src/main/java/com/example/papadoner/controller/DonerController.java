package com.example.papadoner.controller;

import com.example.papadoner.dao.DonerDAO;
import com.example.papadoner.model.Doner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DonerController {

    private DonerDAO donerDAO;

    @Autowired
    public DonerController(DonerDAO donerDAO) {
        this.donerDAO = donerDAO;
    }

    @GetMapping("/getDoner/{name}")
    public Doner getDonerByName(@PathVariable("name") String name) {
        return donerDAO.findByName(name);
    }

}