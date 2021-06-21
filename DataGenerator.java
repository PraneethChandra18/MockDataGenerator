/*
  HOW TO RUN -
  ----------
  -> javac DataGenerator.java
  -> java DataGenerator

    Give required inputs
*/

import java.lang.*;
import java.util.*;
import java.io.*;

class DataGenerator {

  // To scan inputs from terminal
  static Scanner input = new Scanner(System.in);

  // To store the data
  static StringBuilder data = new StringBuilder();

  // To store the list of resources
  static class Resources {
    ArrayList<ArrayList<String>> resources = new ArrayList<ArrayList<String>>(5);

    public Resources() {
        for (int i = 0; i < 5; i++) {
            this.resources.add(new ArrayList<String>());
        }
    }
  }

  // Maps compartment to its list of resources
  static HashMap<String, Resources> resourcesList = new HashMap<String, Resources>();

  // Unique id to each compartment and resources
  static int ocid = 0;

  // Counts the total number of resources
  static int allResources = 0;

  // To give proper indentation to the data created
  public static void indentationLogic(int noOfGaps) {
    for(int i=0;i<noOfGaps;i++)
    {
      data.append(" ");
    }
  }

  // Takes care of the logic related to key "nodes" (in Json format) while creating data
  public static void nodeLogic(int noOfCompartments, int level, int indentation) {

      for(int i=1;i<=noOfCompartments;i++) {
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
        indentationLogic(indentation);
        data.append("{\n");

        indentationLogic(indentation+2);
        data.append("\"id\":");
        data.append("\"ocid.compartment." + ocid + "\",\n");

      	String compartmentId = "ocid.compartment." + ocid;
        ocid = ocid + 1;

        indentationLogic(indentation+2);
        data.append("\"name\":");
        data.append("\"" + compartmentName + "\",\n");

        indentationLogic(indentation+2);
        data.append("\"resources\": [\n");

        resourcesList.put(compartmentId, new Resources());

        // ------------ WRITING THE LIST OF RESOURCES BELONGING TO THE COMPARTMENT ------------
        for(int j=0;j<5;j++)
        {
          if(noOfResources[j]!=0)
          {
            indentationLogic(indentation+4);
            data.append("{\n");

            indentationLogic(indentation+6);
            data.append("\"resourceType\":");
            data.append("\"" + resourceTypes[j] + "\",\n");

            indentationLogic(indentation+6);
            data.append("\"items\": [\n");

            for(int k=0;k<noOfResources[j];k++)
            {
              indentationLogic(indentation+8);

              String r = "ocid." + resourceTypes[j] + "." + ocid;
              data.append("\"" + r + "\"");

              resourcesList.get(compartmentId).resources.get(j).add(r);
              allResources++;

              ocid = ocid + 1;

              if(k == noOfResources[j]-1) data.append("\n");
              else data.append(",\n");
            }

            indentationLogic(indentation+6);
            data.append("]\n");

            indentationLogic(indentation+4);
            data.append("}");

            if(j==last) data.append("\n");
            else data.append(",\n");
          }
        }
        indentationLogic(indentation+2);
        data.append("],\n");

        // ------------ WRITING THE INFORMATION OF CHILDREN BELONGING TO THE COMPARTMENT ------------
        indentationLogic(indentation+2);
        data.append("\"children\": [\n");

        nodeLogic(noOfChildren,level+1,indentation+4); // RECURSION

        indentationLogic(indentation+2);
        data.append("]\n");

        indentationLogic(indentation);
        data.append("}");

        if(i==noOfCompartments) data.append("\n");
        else data.append(",\n");
      }
  }

  // Takes care of the logic related to key "edges" (in Json format) while creating data
  public static void edgeLogic(int indentation) {

    for (String compartment : resourcesList.keySet())
  	{
      Resources eachCompartment = resourcesList.get(compartment);

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
          indentationLogic(indentation);
  				data.append("{\n");

  				indentationLogic(indentation+2);
  				data.append("\"source\":");
  				data.append("\"" + parent.get(j) + "\",\n");

  				indentationLogic(indentation + 2);
  				data.append("\"target\": [\n");

  				int count = ch / p;
  				if (j < l) count++;

          for(int k = 0; k < count; k++)
  				{
  					indentationLogic(indentation + 4);
  					data.append("\"" + children.get(pointer+k) + "\"");

  					if (k == count - 1) data.append("\n");
  					else data.append(",\n");
  				}
  				pointer += count;

  				indentationLogic(indentation+2);
  				data.append("]\n");

  				indentationLogic(indentation);
  				data.append("},\n");
        }
      }
  	}

    data.deleteCharAt(data.length() - 2);
  }


  public static void main(String[] args) {

    data.append("{\n");

    // ------------ NAME FOR THE DATA ------------
    String name;

    System.out.println("Give name to this mock scenario : ");
    name = input.nextLine();

    data.append("  \"name\": ");
    data.append("\"" + name + "\",\n");

    // ------------ WRITING NODES INTO DATA ------------
    data.append("  \"nodes\": [\n");

    int indentation = 4;
    nodeLogic(1,0,indentation);

    data.append("  ],\n");

    // ------------ WRITING EDGES INTO DATA ------------
    data.append("  \"edges\": [\n");

    if(allResources > 0)
    {
      indentation = 4;
      edgeLogic(indentation);
    }

    data.append("  ],\n");

    // ------------ WRITING DESCRIPTION INTO DATA ------------
    String description;

    System.out.println("Give descrition to this mock scenario : ");
    description = input.nextLine();

    data.append("  \"description\": ");
    data.append("\"" + description + "\"\n");
    data.append("}\n");

    // Writing data into file
    File file = new File("mockScenario.txt");
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
        writer.append(data);
    } catch (IOException e) {
      System.out.println(e);
    }
  }
}
