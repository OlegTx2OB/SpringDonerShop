package com.example.papadoner.controller;

import com.example.papadoner.dto.IngredientDto;
import com.example.papadoner.model.Ingredient;
import com.example.papadoner.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/ingredient")
public class IngredientController {

    private final IngredientService mIngredientService;

    @Autowired
    public IngredientController(IngredientService ingredientService) {
        this.mIngredientService = ingredientService;
    }

    @PostMapping
    public ResponseEntity<Void> createIngredient(@RequestBody Ingredient ingredient,
                                                 @RequestParam (required = false) Set<String> donerNames) {
        mIngredientService.createIngredient(ingredient, donerNames);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{name}")
    public ResponseEntity<IngredientDto> getIngredientById(@PathVariable("name") String name) {
        IngredientDto ingredientDto = mIngredientService.getIngredientByName(name);
        return new ResponseEntity<>(ingredientDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredientDto> updateIngredient(
            @PathVariable("id") long id,
            @RequestBody Ingredient updatedIngredient,
            @RequestParam (required = false) Set<String> donerNames) {
        IngredientDto ingredientDto = mIngredientService.updateIngredient(id, updatedIngredient, donerNames);
        return new ResponseEntity<>(ingredientDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable("id") long id) {
        mIngredientService.deleteIngredient(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<Set<IngredientDto>> getAllIngredients() {
        Set<IngredientDto> ingredientDtos = mIngredientService.getAllIngredients();
        return new ResponseEntity<>(ingredientDtos, HttpStatus.OK);
    }
}
