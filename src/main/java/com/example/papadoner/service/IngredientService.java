package com.example.papadoner.service;

import com.example.papadoner.dto.IngredientDto;
import com.example.papadoner.model.Ingredient;

import java.util.List;
import java.util.Set;

public interface IngredientService {

    void createIngredient(Ingredient ingredient, Set<String> donerNames);

    IngredientDto getIngredientByName(String name);

    IngredientDto updateIngredient(long id, Ingredient newIngredient, Set<String> donerNames);

    void deleteIngredient(long id);

    List<IngredientDto> getAllIngredients();
}
