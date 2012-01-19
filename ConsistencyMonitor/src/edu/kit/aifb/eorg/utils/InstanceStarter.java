

	import java.io.*;
	import java.util.List;

	import com.amazonaws.auth.AWSCredentials;
	import com.amazonaws.auth.PropertiesCredentials;
	import com.amazonaws.services.ec2.AmazonEC2;
	import com.amazonaws.services.ec2.AmazonEC2Client;
	import com.amazonaws.services.ec2.model.CreateTagsRequest;
	import com.amazonaws.services.ec2.model.Instance;
	import com.amazonaws.services.ec2.model.Placement;
	import com.amazonaws.services.ec2.model.RunInstancesRequest;
	import com.amazonaws.services.ec2.model.RunInstancesResult;
	import com.amazonaws.services.ec2.model.Tag;

	public class InstanceStarter {

	    private static String location;
        private String [] abc = {"a","b","c"};
        private String [] ac = {"a","c"};
        private String [] ab = {"a","b"};
	    private AmazonEC2 ec2;

	    public static void main(String[] args) throws Exception {
	    	if (args.length<1){
	    		System.out.println("Fehler: Aufruf mit [europe/worldwide]");
	    	}
	    	else{
	    		location = args[0];
	    		new InstanceStarter(location);
	    	}
	    }

	    

	    public InstanceStarter(String location) throws IOException {
	    	
	    	if (location.equalsIgnoreCase("europe")){
	    		startEurope();
	    	}
	    	if (location.equalsIgnoreCase("worldwide")){
	    		startWorldwide();
	    	}
	    }
	    public void startEurope () throws IOException {		
	    	
	        InputStream credentialsAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("AwsCredentials.properties");

	        AWSCredentials credentials = new PropertiesCredentials(credentialsAsStream);
	        ec2 = new AmazonEC2Client(credentials);
	        ec2.setEndpoint("ec2.eu-west-1.amazonaws.com");
	        
	     // CREATE European EC2 INSTANCES Collector
	        RunInstancesRequest runInstancesRequestCollector = new RunInstancesRequest()
	            .withInstanceType("m1.large")
	            .withImageId("ami-953b06e1")
	            .withMinCount(1) //
                .withMaxCount(1) //
                .withSecurityGroupIds("gaestore") //
                .withPlacement(new Placement("eu-west-1a")) //
                .withKeyName("robin")
	        ;
	        
	        RunInstancesResult runInstancesCollector = ec2.runInstances(runInstancesRequestCollector);
	        
	        // TAG EC2 Collector INSTANCES
	        List<Instance> instancesCollector = runInstancesCollector.getReservation().getInstances();
	       
	        for (Instance instance : instancesCollector) {
	          CreateTagsRequest createTagsRequest = new CreateTagsRequest();
	          createTagsRequest.withResources(instance.getInstanceId()) //
	              .withTags(new Tag("Name", "gaecollector"));
	          ec2.createTags(createTagsRequest);
	    
	        }

	     // CREATE European EC2 INSTANCES Writer
	        RunInstancesRequest runInstancesRequestWriter = new RunInstancesRequest()
	            .withInstanceType("t1.micro")
	            .withImageId("ami-953b06e1")
	            .withMinCount(1) //
                .withMaxCount(1) //
                .withSecurityGroupIds("gaestore") //
                .withPlacement(new Placement("eu-west-1a")) //
                .withKeyName("robin")
	        ;
	        
	        RunInstancesResult runInstancesWriter = ec2.runInstances(runInstancesRequestWriter);
	        
	        // TAG EC2 Writer INSTANCES
	        List<Instance> instancesWriter = runInstancesWriter.getReservation().getInstances();

	        for (Instance instance : instancesWriter) {
	          CreateTagsRequest createTagsRequest = new CreateTagsRequest();
	          createTagsRequest.withResources(instance.getInstanceId()) //
	              .withTags(new Tag("Name", "gaewriter"));
	          ec2.createTags(createTagsRequest);
	          
	        }  
	     
	        
	        int idx = 1;
	        // CREATE European EC2 INSTANCES Poller Availability Zones a, b, c
	        for (String s : abc){
		       
		        RunInstancesRequest runInstancesRequest1 = new RunInstancesRequest()
		            .withInstanceType("t1.micro")
		            .withImageId("ami-953b06e1")
		            .withMinCount(4) //
	                .withMaxCount(4) //
	                .withSecurityGroupIds("gaestore") //
	                .withPlacement(new Placement("eu-west-1"+s)) //
	                .withKeyName("robin")
		        ;
		        
		        RunInstancesResult runInstances1 = ec2.runInstances(runInstancesRequest1);
		        
		        // TAG EC2 Poller INSTANCES
		        List<Instance> instances1 = runInstances1.getReservation().getInstances();
		       
		        for (Instance instance : instances1) {
		          CreateTagsRequest createTagsRequest = new CreateTagsRequest();
		          createTagsRequest.withResources(instance.getInstanceId()) //
		              .withTags(new Tag("Name", "gaemon" + idx));
		          ec2.createTags(createTagsRequest);
	
		          idx++;
		        }
	        }
	        System.out.println("Europe started succesfully.");
	    }
	    
	    public void startWorldwide () throws IOException {		
	    	
        InputStream credentialsAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("AwsCredentials.properties");

        AWSCredentials credentials = new PropertiesCredentials(credentialsAsStream);
        ec2 = new AmazonEC2Client(credentials);
        ec2.setEndpoint("ec2.eu-west-1.amazonaws.com");
        
     // CREATE European EC2 INSTANCES Collector
        RunInstancesRequest runInstancesRequestCollector = new RunInstancesRequest()
            .withInstanceType("m1.large")
            .withImageId("ami-953b06e1")
            .withMinCount(1) //
            .withMaxCount(1) //
            .withSecurityGroupIds("gaestore") //
            .withPlacement(new Placement("eu-west-1a")) //
            .withKeyName("robin")
        ;
        
        RunInstancesResult runInstancesCollector = ec2.runInstances(runInstancesRequestCollector);
        
        // TAG EC2 Collector INSTANCES
        List<Instance> instancesCollector = runInstancesCollector.getReservation().getInstances();
       
        for (Instance instance : instancesCollector) {
          CreateTagsRequest createTagsRequest = new CreateTagsRequest();
          createTagsRequest.withResources(instance.getInstanceId()) //
              .withTags(new Tag("Name", "gaecollector"));
          ec2.createTags(createTagsRequest);

        }

     // CREATE European EC2 INSTANCES Writer
        RunInstancesRequest runInstancesRequestWriter = new RunInstancesRequest()
            .withInstanceType("t1.micro")
            .withImageId("ami-953b06e1")
            .withMinCount(1) //
            .withMaxCount(1) //
            .withSecurityGroupIds("gaestore") //
            .withPlacement(new Placement("eu-west-1a")) //
            .withKeyName("robin")
        ;
        
        RunInstancesResult runInstancesWriter = ec2.runInstances(runInstancesRequestWriter);
        
        // TAG EC2 Writer INSTANCES
        List<Instance> instancesWriter = runInstancesWriter.getReservation().getInstances();
       
        for (Instance instance : instancesWriter) {
          CreateTagsRequest createTagsRequest = new CreateTagsRequest();
          createTagsRequest.withResources(instance.getInstanceId()) //
              .withTags(new Tag("Name", "gaewriter"));
          ec2.createTags(createTagsRequest);
  
        }
        
     
        
        // CREATE European EC2 INSTANCES Poller Availability Zones a, b, c
        int idx = 1;
        
        for (String s : abc){
	        RunInstancesRequest runInstancesRequest1 = new RunInstancesRequest()
	            .withInstanceType("t1.micro")
	            .withImageId("ami-953b06e1")
	            .withMinCount(1) //
	            .withMaxCount(1) //
	            .withSecurityGroupIds("gaestore") //
	            .withPlacement(new Placement("eu-west-1"+s)) //
	            .withKeyName("robin")
	        ;
	        
	        RunInstancesResult runInstances1 = ec2.runInstances(runInstancesRequest1);	        
	        
	        // TAG EC2 Poller INSTANCES
	        List<Instance> instances1 = runInstances1.getReservation().getInstances();
	       
	        for (Instance instance : instances1) {
	          CreateTagsRequest createTagsRequest = new CreateTagsRequest();
	          createTagsRequest.withResources(instance.getInstanceId()) //
	              .withTags(new Tag("Name", "gaemon" + idx));
	          ec2.createTags(createTagsRequest);
	    
	          idx++;
	        }
        }
     // CREATE American EC2 INSTANCES Poller Availability Zone a
        
        ec2.setEndpoint("ec2.us-west-1.amazonaws.com");
        
        for (String s : ac){
	        RunInstancesRequest runInstancesRequest2 = new RunInstancesRequest()
	            .withInstanceType("t1.micro")
	            .withImageId("ami-1bd68a5e")
	            .withMinCount(1) //
	            .withMaxCount(1) //
	            .withSecurityGroupIds("robin_california") //
	            .withPlacement(new Placement("us-west-1"+s)) //
	            .withKeyName("robin_california")
	        ;
	        
	        RunInstancesResult runInstances2 = ec2.runInstances(runInstancesRequest2);
	
	     // TAG EC2 Poller INSTANCES
	        List<Instance> instances2 = runInstances2.getReservation().getInstances();
	
	        for (Instance instance : instances2) {
	          CreateTagsRequest createTagsRequest = new CreateTagsRequest();
	          createTagsRequest.withResources(instance.getInstanceId()) //
	              .withTags(new Tag("Name", "gaemon" + idx));
	          ec2.createTags(createTagsRequest);
	
	          idx++;
	        }
        }
     // CREATE Asian EC2 INSTANCES Poller Availability Zones a, b
        
        ec2.setEndpoint("ec2.ap-northeast-1.amazonaws.com");
        
        for (String s : ab){
	        RunInstancesRequest runInstancesRequest3 = new RunInstancesRequest()
	            .withInstanceType("t1.micro")
	            .withImageId("ami-0a44f00b")
	            .withMinCount(1) //
	            .withMaxCount(1) //
	            .withSecurityGroupIds("gae_tokyo") //
	            .withPlacement(new Placement("ap-northeast-1"+s)) //
	            .withKeyName("robin_tokyo")
	        ;
	
	        RunInstancesResult runInstances3 = ec2.runInstances(runInstancesRequest3);
	
	     // TAG EC2 Poller INSTANCES
	        List<Instance> instances3 = runInstances3.getReservation().getInstances();
	
	        for (Instance instance : instances3) {
	          CreateTagsRequest createTagsRequest = new CreateTagsRequest();
	          createTagsRequest.withResources(instance.getInstanceId()) //
	              .withTags(new Tag("Name", "gaemon" + idx));
	          ec2.createTags(createTagsRequest);
	  
	          idx++;
		    }
        }
        System.out.println("Worldwide started succesfully.");
	}
}