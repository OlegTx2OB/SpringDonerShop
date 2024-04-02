package com.example.papadoner.service.impl;

import com.example.papadoner.dto.IngredientDto;
import com.example.papadoner.mapper.IngredientMapper;
import com.example.papadoner.model.Doner;
import com.example.papadoner.model.Ingredient;
import com.example.papadoner.repository.DonerRepository;
import com.example.papadoner.repository.IngredientRepository;
import com.example.papadoner.service.IngredientService;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class IngredientServiceImpl implements IngredientService {
    private final IngredientRepository mIngredientRepository;
    private final DonerRepository mDonerRepository;
    private final IngredientMapper mIngredientMapper;

    @Autowired
    public IngredientServiceImpl(IngredientRepository ingredientRepository,
                                 DonerRepository donerRepository,
                                 IngredientMapper ingredientMapper) {
        this.mIngredientRepository = ingredientRepository;
        this.mDonerRepository = donerRepository;
        this.mIngredientMapper = ingredientMapper;
    }

    @Override
    public IngredientDto createIngredient(Ingredient ingredient, @Nullable Set<String> donerNames) {
        List<Doner> doners = getDoners(donerNames);
        ingredient.getDoners().addAll(doners);
        IngredientDto ingredientDto = mIngredientMapper.toDto(mIngredientRepository.save(ingredient));
        saveIngredientInDoners(ingredient, doners);
        return ingredientDto;
    }

    private void saveIngredientInDoners(Ingredient ingredient, List<Doner> doners) {
        for (Doner doner : doners) {
            doner.getIngredients().add(ingredient);
            mDonerRepository.save(doner);
        }
    }

    @Override
    public IngredientDto getIngredientByName(String name) {
        Optional<Ingredient> ingredient = mIngredientRepository.findByName(name);
        return mIngredientMapper.toDto(ingredient
                .orElseThrow(() -> new EntityNotFoundException("Ingredient with name " + name + " not found")));
    }

    @Override
    public IngredientDto updateIngredient(long id, Ingredient newIngredient, @Nullable Set<String> donerNames) {
        List<Doner> doners = getDoners(donerNames);

        Ingredient oldIngredient = mIngredientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingredient with id " + id + " not found"));
        newIngredient.setId(oldIngredient.getId());

        newIngredient.getDoners().addAll(doners);
        IngredientDto ingredientDto = mIngredientMapper.toDto(mIngredientRepository.save(newIngredient));
        saveIngredientInDoners(newIngredient, doners);
        return ingredientDto;
    }

    @Override
    public void deleteIngredient(long id) {
        Ingredient ingredient = mIngredientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingredient with id " + id + " not found"));
        List<Doner> doners = ingredient.getDoners();

        for (Doner doner : doners) {
            doner.getIngredients().remove(ingredient);
            mDonerRepository.save(doner);
        }
        mIngredientRepository.deleteById(id);
    }

    @Override
    public List<IngredientDto> getAllIngredients() {
        return mIngredientMapper.toDtos(mIngredientRepository.findAll());
    }

    private List<Doner> getDoners(Set<String> donerNames) {
        if (donerNames != null) {
            List<Doner> doners = new ArrayList<>();
            for (String name : donerNames) {
                doners.addAll(mDonerRepository.findDonersByName(name)
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Doner with name " + name + " not found")));
            }
            return doners;
        }
        return new ArrayList<>();
    }
}
