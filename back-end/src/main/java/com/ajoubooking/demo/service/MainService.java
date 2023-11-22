package com.ajoubooking.demo.service;

import com.ajoubooking.demo.controller.MainController;
import org.springframework.stereotype.Service;

@Service
public class MainService {

    private final MainController mainController;

    public MainService(MainController mainController) {
        this.mainController = mainController;
    }

    void separateCallNumber() {

    }

    void binarySearch() {

    }
}
