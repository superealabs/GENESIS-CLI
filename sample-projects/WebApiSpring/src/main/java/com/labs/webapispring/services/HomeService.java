package com.labs.webapispring.services;

import org.springframework.data.domain.Sort;
import com.labs.webapispring.models.Home;
import org.springframework.stereotype.Service;
import com.labs.webapispring.repositories.HomeRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Service
public class HomeService {
    private final HomeRepository homeRepository;

    @Autowired
    public HomeService(HomeRepository homeRepository) {
        this.homeRepository = homeRepository;
    }

    public List<Home> getAllHome() {
        return homeRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public Home getHomeById(Long id) {
        Optional<Home> home = homeRepository.findById(id);
        if (home.isPresent()) {
            return home.get();
        } else {
            throw new RuntimeException("Home not found with id : " + id);
        }
    }

    public Home createHome(Home home) {
        return homeRepository.save(home);
    }

    public Home updateHome(Long id, Home home) {
        Optional<Home> existingHome = homeRepository.findById(id);
        if (existingHome.isPresent()) {
            home.setId(id);
            return homeRepository.save(home);
        } else {
            throw new RuntimeException("Home not found with id : " + id);
        }
    }

    public void deleteHome(Long id) {
        homeRepository.deleteById(id);
    }

}
