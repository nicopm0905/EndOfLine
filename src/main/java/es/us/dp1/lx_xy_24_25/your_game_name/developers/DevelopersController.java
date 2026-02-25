package es.us.dp1.lx_xy_24_25.your_game_name.developers;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.maven.model.Developer;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.us.dp1.lx_xy_24_25.your_game_name.model.Person;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/developers")
@Tag(name = "Developers", description = "API to retrieve developer information from pom.xml")
public class DevelopersController {
    List<Developer> developers;
    
    @GetMapping
    @Operation(summary = "Get developers", description = "Retrieves the list of developers from the project's pom.xml.")
    @ApiResponse(responseCode = "200", description = "List of developers")
    public List<Developer> getDevelopers(){
        if(developers==null)
            loadDevelopers();
        return developers;        
    }

    private void loadDevelopers(){        
        MavenXpp3Reader reader = new MavenXpp3Reader();
        try {
            Model model = reader.read(new FileReader("pom.xml"));
            Person p=null;
            developers=model.getDevelopers();                                            
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
        
    }


}
