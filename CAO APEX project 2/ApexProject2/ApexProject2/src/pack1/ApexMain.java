package pack1;

import java.util.Scanner;

public class ApexMain {

	static int no;
	static int urf_reg_count;
	private static String filename;

	public static void main(String[] args) {

		ApexPipeline a = new ApexPipeline();

		if (args.length < 1) {
			System.out.println("Please enter valid input file");
		}
		filename = args[0];

		Scanner sc = new Scanner(System.in);
		int choice = 0;
		while (choice != 4) {
			System.out.println("\n***********APEX PROJECT 2 MENU***********");
			System.out.println("1.Initialize\n2.Set_URF_size <n>\n3.Simulate\n4.Display\n5.Exit");
			System.out.println("6.Print_map_tables\n7.Print_IQ\n8.Print_ROB\n9.Print_URF");
			System.out.println("10.Print_Memory <a1> <a2>\n11.Print_Stats");
			System.out.println("Please select one of the above choices: ");
			choice = sc.nextInt();
			switch (choice) {
			case 1:
				a.init(args[0]);
				System.out.println("initializing...");
				break;
			case 2:
				System.out.println("Enter the number of registers in the unified register file: ");
				Scanner sz = new Scanner(System.in);
				a.Set_URF_size(sz.nextInt()); 
				break;

			case 3:
				System.out.println("Enter no of cycles...");
				Scanner s = new Scanner(System.in);
				no = s.nextInt();
				a.simulate(no);
				break;
			case 4:
				a.display();
				break;
			case 5:
				a.exit();
				break;
			case 6:
				a.Print_map_tables();
				break;
			case 7:
				a.Print_IQ();
				break;
			case 8:
				break;
			case 9:
				break;
			case 10:
				a.Print_Memory();
				break;
			case 11:
				a.Print_Stats();
				break;
			default:
				System.out.println("Enter valid choice!");
				break;
			}
		}
	}

}
