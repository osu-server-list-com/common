package de.marcandreher.common.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Data;

@Data
public class ServerModel {
    private int id;
    private String name;
    private String url;
    private String safe_name;
    private String safe_categories;
    private boolean online;
    private List<Category> categories = new ArrayList<>();
    private HashMap<String, Integer> stats = new HashMap<>();
    private HashMap<String, String> created = new HashMap<>();
    private HashMap<String, String> customizations = new HashMap<>();
}