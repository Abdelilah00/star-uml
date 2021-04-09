import java.io.IOException;


public class StarUml {
    public static void main(String[] args) throws IOException {
        var entitiesExtractor = new EntitiesExtractor();
        var pckgs = entitiesExtractor.getPackagedEntities("C:\\Users\\Alexis\\IdeaProjects\\star-uml\\src\\Logistica.mdj");

        entitiesExtractor.createEntitiesPackages(pckgs);
        entitiesExtractor.createInterfacesPackages(pckgs);
        entitiesExtractor.createDtosPackages(pckgs);
        entitiesExtractor.createServicesPackages(pckgs);
        entitiesExtractor.createRepositoriesPackages(pckgs);
        entitiesExtractor.createControllersPackages(pckgs);
    }
}

