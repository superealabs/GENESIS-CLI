package genesis.config.langage;

import genesis.config.Constantes;
import genesis.connexion.Credentials;
import genesis.config.CustomChanges;
import genesis.config.CustomFile;
import genesis.connexion.Database;
import genesis.model.Model;
import genesis.view.NavbarLink;
import genesis.view.View;
import genesis.controller.Controller;
import genesis.controller.ControllerField;
import genesis.controller.ControllerMethod;
import genesis.model.Entity;
import genesis.model.EntityField;
import lombok.*;
import utils.FileUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class Language {
    private int id;
    private String name;
    private List<Integer> applicationId;
    private Map<String, String> syntax;
    private Map<String, String> types;
    private Map<String, String> typeParsers;
}
