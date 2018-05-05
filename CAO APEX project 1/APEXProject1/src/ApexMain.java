import java.util.Scanner;



public class ApexMain {

	static int no;
	private static String filename;
	
	public static void main(String[] args) {

		ApexPipeline a =new ApexPipeline();
		
		if(args.length <1)
		{
			System.out.println("Please enter valid input file");
		}
		filename = args[0];

		Scanner sc=new Scanner(System.in);
		int choice=0;
		while(choice!=4)
		{
			System.out.println("Please select one of the below choices:");
			System.out.println("\n1.Initialize\n2.Simulate\n3.Display\n4.Exit\n");
		   choice=sc.nextInt();	
			switch(choice)
			{
			case 1: a.init(args[0]);
			System.out.println("initializing...");
			break;

			case 2: 
			System.out.println("Enter no of cycles..." );
			Scanner s=new Scanner(System.in);
			 no=s.nextInt();
			a.simulate(no);
			break;
			case 3: a.display();
			break;
			case 4:a.exit();
			break;	
			default: 
				System.out.println("enter valid choice");
				break;
			}
		}
	}

}
