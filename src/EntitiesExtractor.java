import classes.Attribute;
import classes.Entity;
import classes.Interface;
import classes.Package;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class EntitiesExtractor {
    private final String appName = "logistica";
    private final String entities = "domains";
    private final String controllers = "controllers";
    private final String services = "services";
    private final String repositories = "repositories";
    private final String dtos = "dtos";

    public List<Package> getPackagedEntities(String path) throws IOException {
        var pckgs = new ArrayList<Package>();

        var objectMapper = new ObjectMapper();
        var json = Files.readString(Path.of(path));
        var projectNode = objectMapper.readTree(json);

        var umlModel = projectNode.get("ownedElements").get(0).get("ownedElements");

        for (var umlM : umlModel) {
            //get packages
            if (umlM.get("_type").asText().equals("UMLPackage")) {
                var pckg = new Package();

                pckg.setName(umlM.get("name").asText());

                for (var item : umlM.get("ownedElements")) {
                    //get classes
                    if (item.get("_type").asText().equals("UMLClass")) {
                        var entity = new Entity();

                        //get name of classes
                        entity.setName(item.get("name").asText());
                        //get attributes
                        for (var attribute : item.get("attributes")) {
                            var attr = new Attribute();
                            attr.setName(attribute.get("name").asText());
                            var type = attribute.get("type");
                            if (type == null || type.asText().equals(""))
                                attr.setType("String");
                            else
                                attr.setType(type.asText());
                            entity.getAttributes().add(attr);
                        }
                        pckg.getEntities().add(entity);

                    } else if (item.get("_type").asText().equals("UMLInterface")) {
                        var interfa = new Interface();
                        //get name of classes
                        interfa.setName(item.get("name").asText());
                        //get attributes
                        for (var attribute : item.get("attributes")) {
                            interfa.getValues().add(attribute.get("name").asText());
                        }
                        pckg.getInterfaces().add(interfa);
                    }
                    pckgs.add(pckg);
                }
            }
        }
        return pckgs;
    }

    void createEntitiesPackages(List<Package> packages) throws IOException {
        createDirectory(entities);
        for (var pckg : packages)
            createEntitiesOf(pckg);
    }

    private void createEntitiesOf(Package pckg) throws IOException {
        createDirectory(entities + '/' + pckg.getName());

        for (var entity : pckg.getEntities()) {
            FileWriter myWriter = new FileWriter(entities + '/' + pckg.getName() + "/" + entity.getName() + ".java");
            myWriter.write("import com.alexy.models.BaseEntity;\n" +
                    "import lombok.AllArgsConstructor;\n" +
                    "import lombok.Getter;\n" +
                    "import lombok.NoArgsConstructor;\n" +
                    "import lombok.Setter;\n" +
                    "import org.hibernate.annotations.Fetch;\n" +
                    "import org.hibernate.annotations.FetchMode;\n" +
                    "\n" +
                    "import javax.persistence.*;\n" +
                    "import javax.validation.constraints.NotBlank;\n" +
                    "import java.util.List;\n" +
                    "\n" +
                    "@Entity\n" +
                    "@Getter\n" +
                    "@Setter\n" +
                    "@AllArgsConstructor\n" +
                    "@NoArgsConstructor\n" +
                    "@Table(name = \"" + entity.getName().toLowerCase() + "\")\n" +
                    "public class " + entity.getName() + " extends BaseEntity {\n");
            for (var attr : entity.getAttributes())
                myWriter.write("private " + attr.getType() + " " + attr.getName() + ";\n");
            myWriter.write("}");
            myWriter.close();
        }

    }

    //interfaces
    void createInterfacesPackages(List<Package> packages) throws IOException {
        for (var pckg : packages)
            createInterfacesOf(pckg);
    }

    private void createInterfacesOf(Package pckg) throws IOException {
        for (var intrfc : pckg.getInterfaces()) {
            FileWriter myWriter = new FileWriter(entities + '/' + pckg.getName() + "/" + intrfc.getName() + ".java");
            myWriter.write("import lombok.Getter;\n" +
                    "\n" +
                    "@Getter\n" +
                    "public enum " + intrfc.getName() + " {\n");
            for (var attr : intrfc.getValues())
                myWriter.write(attr + ",\n");
            myWriter.write("}");
            myWriter.close();
        }

    }

    //create dtos
    void createDtosPackages(List<Package> packages) throws IOException {
        createDirectory(dtos);
        for (var pckg : packages)
            createDtosOf(pckg);
    }

    private void createDtosOf(Package pckg) throws IOException {
        createDirectory(dtos + '/' + pckg.getName());

        ///////////////////////////XCreateDto
        for (var entity : pckg.getEntities()) {
            createDirectory(dtos + '/' + pckg.getName() + "/" + entity.getName());
            FileWriter myWriter = new FileWriter(dtos + '/' + pckg.getName() + "/" + entity.getName() + "/" + entity.getName() + "CreateDto.java");
            myWriter.write("import com.alexy.models.BaseDto;\n" +
                    "import lombok.Getter;\n" +
                    "import lombok.Setter;\n" +
                    "\n" +
                    "@Getter\n" +
                    "@Setter\n" +
                    "public class " + entity.getName() + "CreateDto extends BaseDto {\n");
            for (var attr : entity.getAttributes())
                myWriter.write("private " + attr.getType() + " " + attr.getName() + ";\n");
            myWriter.write("}");
            myWriter.close();

            //////////////////////////XUpdateDto
            myWriter = new FileWriter(dtos + '/' + pckg.getName() + "/" + entity.getName() + "/" + entity.getName() + "UpdateDto.java");
            myWriter.write("package com.logistica.dtos;\n" +
                    "\n" +
                    "import com.alexy.models.BaseDto;\n" +
                    "import lombok.Getter;\n" +
                    "import lombok.Setter;\n" +
                    "\n" +
                    "@Getter\n" +
                    "@Setter\n" +
                    "public class " + entity.getName() + "UpdateDto extends BaseDto {\n");
            for (var attr : entity.getAttributes())
                myWriter.write("private " + attr.getType() + " " + attr.getName() + ";\n");
            myWriter.write("}");
            myWriter.close();

            ///////////////////////////////////////////////////////
            myWriter = new FileWriter(dtos + '/' + pckg.getName() + "/" + entity.getName() + "/" + entity.getName() + "Dto.java");
            myWriter.write("package com.logistica.dtos;\n" +
                    "\n" +
                    "import com.alexy.models.BaseDto;\n" +
                    "import lombok.Getter;\n" +
                    "import lombok.Setter;\n" +
                    "\n" +
                    "@Getter\n" +
                    "@Setter\n" +
                    "public class " + entity.getName() + "Dto extends BaseDto {\n");
            for (var attr : entity.getAttributes())
                myWriter.write("private " + attr.getType() + " " + attr.getName() + ";\n");
            myWriter.write("}");
            myWriter.close();
        }
    }

    //services
    void createServicesPackages(List<Package> packages) throws IOException {
        createDirectory(dtos);
        for (var pckg : packages)
            createServicesOf(pckg);
    }

    private void createServicesOf(Package pckg) throws IOException {
        createDirectory(services + '/' + pckg.getName());

        ///////////////////////////Service
        for (var entity : pckg.getEntities()) {
            createDirectory(services + '/' + pckg.getName() + "/" + entity.getName());

            FileWriter myWriter = new FileWriter(services + '/' + pckg.getName() + "/" + entity.getName() + "/" + entity.getName() + "Service.java");
            myWriter.write("import com.alexy.services.BaseCrudServiceImpl;\n" +
                    "import com.alexy.services.IBaseCrudService;\n" +
                    "import com." + appName + ".domains." + pckg.getName() + "." + entity.getName() + ";\n" +
                    "import com." + appName + ".dtos." + pckg.getName() + "." + entity.getName() + "." + entity.getName() + "CreateDto;\n" +
                    "import com." + appName + ".dtos." + pckg.getName() + "." + entity.getName() + "." + entity.getName() + "Dto;\n" +
                    "import com." + appName + ".dtos." + pckg.getName() + "." + entity.getName() + "." + entity.getName() + "UpdateDto;\n" +
                    "import org.springframework.stereotype.Service;\n" +
                    "\n" +
                    "@Service\n" +
                    "public class " + entity.getName() + "Service extends BaseCrudServiceImpl<" + entity.getName() + ", " + entity.getName() + "Dto, " + entity.getName() + "CreateDto, " + entity.getName() + "UpdateDto> implements I" + entity.getName() + "Service {\n" +
                    "\n" +
                    "    public " + entity.getName() + "Service() {\n" +
                    "        super(" + entity.getName() + ".class, " + entity.getName() + "Dto.class, " + entity.getName() + "CreateDto.class, " + entity.getName() + "UpdateDto.class);\n" +
                    "    }\n" +
                    "}");
            myWriter.close();

            //////////////////////////IService
            myWriter = new FileWriter(services + '/' + pckg.getName() + "/" + entity.getName() + "/I" + entity.getName() + "Service.java");
            myWriter.write("import com.alexy.services.IBaseCrudService;\n" +
                    "import com." + appName + ".domains." + pckg.getName() + "." + entity.getName() + ";\n" +
                    "import com." + appName + ".dtos." + pckg.getName() + "." + entity.getName() + "." + entity.getName() + "CreateDto;\n" +
                    "import com." + appName + ".dtos." + pckg.getName() + "." + entity.getName() + "." + entity.getName() + "Dto;\n" +
                    "import com." + appName + ".dtos." + pckg.getName() + "." + entity.getName() + "." + entity.getName() + "UpdateDto;\n" +
                    "import org.springframework.stereotype.Service;\n" +
                    "\n" +
                    "public interface I" + entity.getName() + "Service extends IBaseCrudService<" + entity.getName() + ", " + entity.getName() + "Dto, " + entity.getName() + "CreateDto, " + entity.getName() + "UpdateDto> {\n" +
                    "\n" +
                    "}");
            myWriter.close();
        }
    }

    //repositories
    void createRepositoriesPackages(List<Package> packages) throws IOException {
        createDirectory(dtos);
        for (var pckg : packages)
            createRepositoriesOf(pckg);
    }

    private void createRepositoriesOf(Package pckg) throws IOException {
        createDirectory(repositories + '/' + pckg.getName());

        ///////////////////////////Service
        for (var entity : pckg.getEntities()) {
            FileWriter myWriter = new FileWriter(repositories + '/' + pckg.getName() + "/" + "/I" + entity.getName() + "Repository.java");
            myWriter.write("import com.alexy.repositories.IBaseJpaRepository;\n" +
                    "import org.springframework.stereotype.Repository;\n" +
                    "import com." + appName + ".domains." + pckg.getName() + "." + entity.getName() + ";\n" +
                    "\n" +
                    "@Repository\n" +
                    "public interface I" + entity.getName() + "Repository extends IBaseJpaRepository<" + entity.getName() + "> {\n" +
                    "}");
            myWriter.close();
        }
    }

    //controllers
    void createControllersPackages(List<Package> packages) throws IOException {
        createDirectory(dtos);
        for (var pckg : packages)
            createControllersOf(pckg);
    }

    private void createControllersOf(Package pckg) throws IOException {
        createDirectory(controllers + '/' + pckg.getName());

        ///////////////////////////Service
        for (var entity : pckg.getEntities()) {
            FileWriter myWriter = new FileWriter(controllers + '/' + pckg.getName() + "/" + "/" + entity.getName() + "Controller.java");
            myWriter.write("import com.alexy.controllers.BaseCrudController;\n" +
                    "import org.springframework.web.bind.annotation.RequestMapping;\n" +
                    "import org.springframework.web.bind.annotation.RestController;\n" +
                    "import com." + appName + ".domains." + pckg.getName() + "." + entity.getName() + ";\n" +
                    "import com." + appName + ".dtos." + pckg.getName() + "." + entity.getName() + "." + entity.getName() + "CreateDto;\n" +
                    "import com." + appName + ".dtos." + pckg.getName() + "." + entity.getName() + "." + entity.getName() + "Dto;\n" +
                    "import com." + appName + ".dtos." + pckg.getName() + "." + entity.getName() + "." + entity.getName() + "UpdateDto;\n" +
                    "\n" +
                    "@RestController\n" +
                    "@RequestMapping(\"api/" + entity.getName().toLowerCase() + "s\")\n" +
                    "public class " + entity.getName() + "Controller extends BaseCrudController<" + entity.getName() + ", " + entity.getName() + "Dto, " + entity.getName() + "CreateDto, " + entity.getName() + "UpdateDto> {\n" +
                    "}");
            myWriter.close();
        }
    }


    private void createDirectory(String name) throws IOException {
        Path path = Paths.get(name);
        Files.createDirectories(path);
    }
}
