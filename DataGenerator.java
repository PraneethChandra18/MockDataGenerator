/*
  HOW TO RUN -
  ----------
  -> javac -cp jackson/* DataGenerator.java
  -> java --module-path jackson/ --add-modules ALL-MODULE-PATH DataGenerator

    Give required inputs
*/

import java.lang.*;
import java.util.*;
import java.io.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;


class Data {
    public String name;
    public List<Compartment> nodes;
    public List<Edge> edges;
    public String description;

    public Data() {
      nodes = new ArrayList<Compartment>();
      edges = new ArrayList<Edge>();
    }
}

class Compartment {
    public String id;
    public String name;
    public List<Resources> resources;
    public List<Compartment> children;

    public Compartment() {
      resources = new ArrayList<Resources>();
      children = new ArrayList<Compartment>();
    }
}

class Resources {
    public String resourceType;
    public List<String> items;

    public Resources() {
      items = new ArrayList<String>();
    }
}

class Edge {
    public String source;
    public List<String> target;

    public Edge() {
      target = new ArrayList<String>();
    }
}


// To store the list of resources
class ResourcesList {
  ArrayList<ArrayList<String>> resources = new ArrayList<ArrayList<String>>(5);

  public ResourcesList() {
      for (int i = 0; i < 5; i++) {
          this.resources.add(new ArrayList<String>());
      }
  }
}

class DataGenerator {

  // To store output
  static Data outputData = new Data();

  // To scan inputs from terminal
  static Scanner input = new Scanner(System.in);

  // Maps compartment to its list of resources
  static HashMap<String, ResourcesList> resourcesList = new HashMap<String, ResourcesList>();

  // Unique id to each compartment and resources
  static int ocid = 0;

  // Counts the total number of resources
  static int allResources = 0;


  // Takes care of the logic related to key "nodes" (in Json format) while creating data
  public static List<Compartment> nodeLogic(int noOfCompartments, int level) {

      List<Compartment> listOfCompartments = new ArrayList<Compartment> ();
      for(int i=1;i<=noOfCompartments;i++) {

        Compartment comp = new Compartment();

        int noOfChildren;
        int[] noOfResources = {0,0,0,0,0};
        int last = 0;
        String[] resourceTypes = {"VCN","subnet","instance","NIC","blockVolume"};

        String compartmentName;

        System.out.println("For " + level + "-" + i + "th compartment - ");
        System.out.println("-------------------");

        // ------------ TAKING INPUTS ------------
        System.out.println("Enter the name for the compartment :");
        compartmentName = input.nextLine();

        System.out.println("Enter the number of children :");
        noOfChildren = input.nextInt();
        input.nextLine();

        for(int j=0;j<5;j++) {
          System.out.println("Enter the number of " + resourceTypes[j] + "s :");
          noOfResources[j] = input.nextInt();
          input.nextLine();
          if(noOfResources[j] == 0) break;
          last = j;
        }

        // ------------ WRITING DATA OF EACH COMPARTMENT (NODE) ------------

      	String compartmentId = "ocid.compartment." + ocid;
        ocid = ocid + 1;

        comp.id = compartmentId;
        comp.name = compartmentName;

        resourcesList.put(compartmentId, new ResourcesList());

        // ------------ WRITING THE LIST OF RESOURCES BELONGING TO THE COMPARTMENT ------------
        for(int j=0;j<5;j++)
        {
          if(noOfResources[j]!=0)
          {
            Resources res = new Resources();
            res.resourceType = resourceTypes[j];

            for(int k=0;k<noOfResources[j];k++)
            {
              String r = "ocid." + resourceTypes[j] + "." + ocid;
              res.items.add(r);

              resourcesList.get(compartmentId).resources.get(j).add(r);
              allResources++;

              ocid = ocid + 1;
            }
            comp.resources.add(res);
          }
        }

        // ------------ WRITING THE INFORMATION OF CHILDREN BELONGING TO THE COMPARTMENT ------------
        comp.children = nodeLogic(noOfChildren,level+1); // RECURSION

        listOfCompartments.add(comp);
      }

      return listOfCompartments;
  }

  // Takes care of the logic related to key "edges" (in Json format) while creating data
  public static List<Edge> edgeLogic() {

    List<Edge> listOfEdges = new ArrayList<Edge> ();
    for (String compartment : resourcesList.keySet())
  	{
      ResourcesList eachCompartment = resourcesList.get(compartment);

      int s = eachCompartment.resources.size();
      for(int i=0;i < s-1;i++)
      {
        ArrayList<String> parent = eachCompartment.resources.get(i);
        ArrayList<String> children = eachCompartment.resources.get(i+1);

        int p = parent.size();
			  int ch = children.size();

        int l = 0;
        if(p > 0) l = ch % p;

		    int pointer = 0;

        // ------------ WRITING SOURCE - TARGET PAIRS ------------
        for(int j = 0; j < p; j++)
        {
          Edge edge = new Edge();
  				edge.source = parent.get(j);

  				int count = ch / p;
  				if (j < l) count++;

          for(int k = 0; k < count; k++)
  				{
  					edge.target.add(children.get(pointer+k));
  				}
  				pointer += count;

          listOfEdges.add(edge);
        }
      }
  	}

    return listOfEdges;
  }


  public static void main(String[] args) {

    // ------------ NAME FOR THE DATA ------------
    String name;

    System.out.println("Give name to this mock scenario : ");
    name = input.nextLine();

    outputData.name = name;

    // ------------ WRITING NODES INTO DATA ------------
    outputData.nodes = nodeLogic(1,0);

    // ------------ WRITING EDGES INTO DATA ------------

    if(allResources > 0)
    {
      outputData.edges = edgeLogic();
    }

    // ------------ WRITING DESCRIPTION INTO DATA ------------
    String description;

    System.out.println("Give descrition to this mock scenario : ");
    description = input.nextLine();

    outputData.description = description;

    try {
      ObjectMapper objectMapper = new ObjectMapper();

      String jsonInStringPretty = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(outputData);
      System.out.println(jsonInStringPretty);
    } catch (JsonGenerationException e) {
           e.printStackTrace();
    } catch (JsonMappingException e) {
           e.printStackTrace();
    } catch (IOException e) {
           e.printStackTrace();
    }

    // System.out.println(outputData.toString());

    // Writing data into file
    // File file = new File("mockScenario.txt");
    // try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
    //     writer.append(data);
    // } catch (IOException e) {
    //   System.out.println(e);
    // }
  }
}
