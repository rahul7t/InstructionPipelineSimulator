import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
public class ApexPipeline {

	private static int cycle_no=0;
	private static Integer[] Mem = new Integer[4000];
	private static int PC=0,new_PC=0;
	private static Map<String, Integer> registers = new HashMap<>();
	private static ArrayList<String> InstructionList = new ArrayList<String>();
	private static String[][] pipeline_array = new String[500][8];
	private static String fetchedinstr;
	private static boolean isFetch,isDecode,isExecute,isMem,isWB,lsflag,invalid_PC,fetchStalled,stalled,HALTFlag,loadflag,zeroflag,BRANCH_TAKEN,JUMPFLAG,isBranchInstr,isALU1,isBranchdone;
	private static Queue<String> cycleStatus =new LinkedList<>();
	private static String decodeInstr;
	private static String executeInstr;
	private static String memInstr,wbInstr,delay;
	private static List<Boolean> dependencies=new ArrayList<Boolean>() ;
	private static int cycle;
	private static String ex_dest_dep;
	private static String decodestalled = null;
	void init(String filename)
	{
		PC=0;
		cycle=0;
		//pipelinedInstr[0]=pipelinedInstr[1]="";
		isFetch=isDecode=isExecute=isMem=isWB=invalid_PC=lsflag=fetchStalled=stalled=HALTFlag=zeroflag=loadflag=BRANCH_TAKEN=JUMPFLAG=isBranchInstr=isALU1=isBranchdone=false;
		for(int i=0;i<Mem.length;i++)
		{
			Mem[i]=-1;
		}

		/*for(String i : registers.keySet())
	{
		registers.put("R"+i, 0);
		System.out.println("THe reg contains"+registers.get("R"+i));
	}*/
		registers.put("R0", 0);
		registers.put("R1", 0);
		registers.put("R2", 0);
		registers.put("R3", 0);
		registers.put("R4", 0);
		registers.put("R5", 0);
		registers.put("R6", 0);
		registers.put("R7", 0);
		registers.put("R8", 0);
		registers.put("R9", 0);
		registers.put("R10", 0);
		registers.put("R11", 0);
		registers.put("R12", 0);
		registers.put("R13", 0);
		registers.put("R14", 0);
		registers.put("R15", 0);
		registers.put("R16", 0);
		registers.put("X", 0);
		//System.out.println("R4 contents are:"+registers.get("R4"));
		/*System.out.println("Initializing the pipeline stages");
		stages.put("F",null);
		stages.put("D/RF",null);
		stages.put("EX",null);
		stages.put("MEM",null);
		stages.put("WB", null);*/
		//delay="BR_DELAY";
		for(int i = 0;i<500;i++)
			for(int j = 0;j<= 7;j++)
			{
				pipeline_array[i][j] = "IDLE";
			}

		ex_dest_dep=null;
		fetchStalled=false;
		for(int d=0;d<8;d++)
		{

			dependencies.add(false);
		}

		System.out.println("All registers,memory,stages and flags are initialized");

		prefetch(filename);

	//	System.out.println("Input file read...in prefetch");


	}	

	void simulate(int no)
	{
		cycle_no=no;

		while(cycle<=no)
		{
		//	System.out.println("THe cycle is :"+cycle);
		//	System.out.println("simulate enter");
		//	System.out.println("fetch");
			writeback();
			memory();
		//	System.out.println("branch inst flag value is:"+isBranchInstr);
			delay();
		////	System.out.println("branch inst flag value is:"+isBranchInstr);

			branch();
		//	System.out.println("branch inst flag value is:"+isBranchInstr);

			ALU2();
		//	System.out.println("branch inst flag value is:"+isBranchInstr);

			ALU1();
			
		//	System.out.println("branch inst flag value is:"+isBranchInstr);

			decode();
		//	System.out.println("branch inst flag value is:"+isBranchInstr);

			fetch();

			//stages.put("EX", inst);
		//	printlist.add(stages);

			cycle++;

		}
		if(cycle != no && (invalid_PC ||HALTFlag)) {
			display();
			if(HALTFlag)
				System.out.println("\nSimulation ended due to HALT instruction...");
			if(invalid_PC)
				System.out.println("\nSimulation ended due to bad PC value..." + PC);
			System.exit(0);
		}
	}


	private static void delay()
	{
		if(isBranchdone)
		{
			pipeline_array[cycle][5]=pipeline_array[cycle-1][4];

		}
		isBranchdone=false;
	}

	void fetch()
	{


		if(!fetchStalled && !JUMPFLAG)
		{
			//System.out.println(jumpFlag);


			if(stalled)
			{
				//System.out.println("Stalled is true. Setting fetch stalled to true");
				fetchStalled = true;
				
			}
			if(PC>InstructionList.size())
			{
				invalid_PC=true;
			}
			else if(PC<InstructionList.size() && !BRANCH_TAKEN && !JUMPFLAG) {
			//	System.out.println("PC value is:"+PC+"instrn list size is:"+InstructionList.size());

				fetchedinstr=InstructionList.get(PC);
				PC++;
			//	System.out.println("instrn fetched is:"+fetchedinstr);
				pipeline_array[cycle][0]=fetchedinstr; 

			}
		}else
		{
			//not reading
			System.out.println("not reading as stalled");
			pipeline_array[cycle][0]=fetchedinstr+" stalled";
		}

		if(BRANCH_TAKEN || JUMPFLAG)
		{
//			System.out.println("JUMFLAG In fetch:"+JUMPFLAG);
			fetchedinstr=InstructionList.get(PC);
			PC++;
			fetchedinstr="SQUASH"+" "+fetchedinstr;
			pipeline_array[cycle][0]=fetchedinstr;
			//pipeline_array[cycle][1]=decodeInstr;
//			System.out.println("branch taken & instr in fetch is:"+fetchedinstr);
			PC=new_PC;
//			System.out.println("PC value after jump:"+PC);

			
		}
		if(BRANCH_TAKEN)
			BRANCH_TAKEN=false;
		if(JUMPFLAG)
			JUMPFLAG=false;
		isFetch=true;
		decodestalled=fetchedinstr;
	}




	void prefetch(String filename)
	{
		System.out.println("prefetch enter:");
		Scanner s;
		try {
			s = new Scanner(new File(filename));


			/*if(PC > 0) {
				for(int i = 0; i < PC; i++)
					InstructionList.add(i, null);
			}*/

			while (s.hasNextLine())
			{
				String str = s.nextLine();
				InstructionList.add((str.replaceAll("#", "").replaceAll(",", "")));
				//System.out.println("the instructions is:"+s.nextLine());
			}
			s.close();
			Iterator<String> instrIterator = InstructionList.iterator();
			while (instrIterator.hasNext()) 
			{
				System.out.println(instrIterator.next());
			}
	//		System.out.println("Instruction count read from file: "+InstructionList.size());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}




	void decode(){
	//	System.out.println("IN deocde stage");
		
		/*if(!stalled)
	{*/
		//fetchStalled = false;
		//		isBranchInstr=false;   //undo it later
		//	lsflag=false;
		//if(isFetch && !stalled)
		if(!stalled)
		{ 
			/*if(!isDecode)
			{*/
			fetchStalled=false;
			if(cycle!=0)
			{
				
				decodeInstr=pipeline_array[cycle-1][0];   //fetchedinstr; 
				//pipeline_array[cycle-1][0];
				
				//System.out.println("cycle is in decode"+cycle);
			}
			else
			{
				
				decodeInstr=pipeline_array[cycle][0];
		//	decodestalled=decodeInstr;
			}
//System.out.println("in decodestalled"+decodeInstr);
	//		System.out.println("in decode-----");
		}	
		else if(stalled)
		{
			decodeInstr=decodestalled ;//pipeline_array[cycle-2][0];
		}
		if(decodeInstr != null && !BRANCH_TAKEN && !JUMPFLAG)
		{
			
			if(!checkDependency()){
				stalled=false;
			//	System.out.println("decodeInstr: "+decodeInstr);

				//stages.put("D/RF",decodeInstr);
				String[] token=decodeInstr.split(" ");
				//ReadFile record=new ReadFile(token[0],token[1],token[2]);
				String opcode=token[0];
				if(opcode.equalsIgnoreCase("ADD") || opcode.equalsIgnoreCase("SUB") || opcode.equalsIgnoreCase("MUL") || opcode.equalsIgnoreCase("DIV") || opcode.equalsIgnoreCase("AND") || opcode.equalsIgnoreCase("OR"))
					decodeArith();
				else if(opcode.equalsIgnoreCase("MOV") || opcode.equalsIgnoreCase("MOVC"))
					decodeMov();
				else if(opcode.equalsIgnoreCase("LOAD") || opcode.equalsIgnoreCase("STORE"))
					decodeloadStore();
				else if(opcode.equalsIgnoreCase("JUMP") || opcode.equalsIgnoreCase("BAL") || opcode.equalsIgnoreCase("BNZ") || opcode.equalsIgnoreCase("BZ"))
					decodeBranch();
				else if(opcode.equalsIgnoreCase("SQUASH"))
					pipeline_array[cycle][1]=decodeInstr;
			}
			else
			{
				pipeline_array[cycle][1]=decodeInstr+" "+"stalled";
				stalled=true;
			}
		}

		//}


	//	System.out.println("JUMPFLAG in decode:"+JUMPFLAG);

		if(BRANCH_TAKEN || JUMPFLAG)                               //if branch taken then squashing the instructions in fetch and decode
		{
			//	pipeline_array[cycle][2]=executeInstr;
			//String inst=pipeline_array[cycle][1];
			decodeInstr="SQUASH"+" "+decodeInstr;
			System.out.println("branch taken & instr in decode is:"+decodeInstr);
			//pipeline_array[cycle][1]=inst;
			//fetchedinstr=pipeline_array[cycle][0];
			/*	fetchedinstr="SQUASH"+" "+fetchedinstr;
		pipeline_array[cycle][0]=fetchedinstr;*/
			pipeline_array[cycle][1]=decodeInstr;

		}
	}

	private void decodeMov() {                                  //decoding the MOV and MOVC instruction
		System.out.println("in movOp... ");
		// TODO Auto-generated method stub
		String[] token=decodeInstr.split(" ");
		String opcode=token[0];
		int src1=0;
		if(opcode.equalsIgnoreCase("MOVC"))
		{
			//stages.put("D/RF",decodeInstr);
			pipeline_array[cycle][1]=decodeInstr;
			isDecode=true;
		}
		if(opcode.equalsIgnoreCase("MOV"))
		{
			System.out.println("In MOV code");
			if(token[2].charAt(0)=='R' || token[2].charAt(0)=='X' ){
				src1 = registers.get(token[2]);
			}
			else {
				src1 = Integer.parseInt(token[2]);
			}



			decodeInstr=opcode+" "+token[1]+" "+src1;

		//	System.out.println("exiting  MOV instruction:decoded instrn is"+decodeInstr);	
			//stages.put("D/RF",decodeInstr);				
			pipeline_array[cycle][1]=decodeInstr;
			isDecode=true;
		}
	}

	private void decodeloadStore() {                                 //decoding load store instructions
		// TODO Auto-generated method stub
		int src1;
		int src2,dest;
		String[] token=decodeInstr.split(" ");
		String opcode=token[0];
		if(opcode.equalsIgnoreCase("LOAD"))
		{
			src1=registers.get(token[2]);
			/*if(registers.containsKey(token[3]))
		{
			//src1=registers.get(CharAt(token[3]));
			//String src2=token[3].replaceAll("R", "") ;
			src2=registers.get(token[3]);
		}*/
			if(token[3].charAt(0)=='R' || token[3].charAt(0)=='X' ){   //checking if token[3] is literal or register value
				dest = registers.get(token[3]);
				loadflag=true;
			}
			else {
				dest = Integer.parseInt(token[3]);
			}
			decodeInstr=opcode+" "+token[1]+" "+src1+" "+dest;
			pipeline_array[cycle][1]=decodeInstr;
			isDecode=true;
			//lsflag=true;
		}
		if(opcode.equalsIgnoreCase("STORE"))
		{
			src1=registers.get(token[1]);
			if(token[3].charAt(0)=='R' || token[3].charAt(0)=='X' ){
				dest = registers.get(token[3]);
			}
			else {
				dest = Integer.parseInt(token[3]);
			}
			//dest=Integer.parseInt(token[1]);
			src2=registers.get(token[2]);
			decodeInstr=opcode+" "+src1+" "+src2+" "+dest;
			pipeline_array[cycle][1]=decodeInstr;
			isDecode=true;
			//	lsflag=true;
		}
		///lasttwoInstr[0]
	}

	private void decodeBranch() {                                  //decoding branch instructions and setting isBranchInstr to true
		// TODO Auto-generated method stub
		String[] token=decodeInstr.split(" ");
		if(token[0].equalsIgnoreCase("JUMP"))
		{
			int src1 = registers.get(token[1]);
			decodeInstr=token[0]+" "+src1+" "+token[2];
			pipeline_array[cycle][1]=decodeInstr;
			//JUMPFLAG=true;
			isBranchInstr=true;
		}
		else if(token[0].equalsIgnoreCase("BAL") )
		{
			int src1 = registers.get(token[1]);
			decodeInstr=token[0]+" "+src1+" "+token[2];
			pipeline_array[cycle][1]=decodeInstr;
			isBranchInstr=true;
		}
		else if(token[0].equalsIgnoreCase("BZ") || token[0].equalsIgnoreCase("BNZ"))
		{
			pipeline_array[cycle][1]=decodeInstr;
			System.out.println("decodeInstr");
			isBranchInstr=true;
		}
		else if(token[0].equalsIgnoreCase("SQUASH"))
		{
			pipeline_array[cycle][1]=decodeInstr;	
		}

	}


	void ALU1()
	{

		System.out.println("cycle is:"+cycle+" isDecode: "+isDecode+" isBranchInstr"+isBranchInstr+" stalled:"+stalled);
		if(isDecode && !isBranchInstr && !stalled)
		{ 
			isALU1=true;
			if(cycle!=0)
			{
				executeInstr=pipeline_array[cycle-1][1];
				//pipeline_array[cycle-1][2];
				//executeInstr=;	
				System.out.println("cycle is in execute & execute instruction is:"+cycle+" "+executeInstr);
			}
			else
			{executeInstr=pipeline_array[cycle][1];
			//	executeInstr=decodeInstr;	
			}
			
			String [] token=executeInstr.split(" ");
			String opcode=token[0];
			//ex_dest_dep=token[1];
			/*if(opcode.equalsIgnoreCase("ADD") || opcode.equalsIgnoreCase("SUB") || opcode.equalsIgnoreCase("MUL") || opcode.equalsIgnoreCase("DIV") || opcode.equalsIgnoreCase("AND") || opcode.equalsIgnoreCase("MOV") || opcode.equalsIgnoreCase("MOVC")
			||	opcode.equalsIgnoreCase("LOAD") || opcode.equalsIgnoreCase("STORE"))
					{	
			 */
			if(opcode.equalsIgnoreCase("ADD") || opcode.equalsIgnoreCase("SUB") || opcode.equalsIgnoreCase("MUL") || opcode.equalsIgnoreCase("DIV") || opcode.equalsIgnoreCase("AND")){

				ex_dest_dep=token[1];
				int val=Character.getNumericValue(token[1].charAt(1));
				dependencies.set(val,true);

				execarith();
				
			}

			else if(opcode.equalsIgnoreCase("MOV") || opcode.equalsIgnoreCase("MOVC")){
				//nothing to be performed here

				//stages.put("EXE", executeInstr);
				System.out.println("setting dependency for MOVC:");

				ex_dest_dep=token[1];
				int val=Character.getNumericValue(token[1].charAt(1));
				System.out.println("setting dependency for MOVC:"+" "+val);
				dependencies.set(val,true);
				pipeline_array[cycle][2]=executeInstr;
				isExecute=true;
			}

			else if(opcode.equalsIgnoreCase("LOAD") || opcode.equalsIgnoreCase("STORE"))
			{
				if(opcode.equalsIgnoreCase("LOAD")){
					int val=Character.getNumericValue(token[1].charAt(1));
					ex_dest_dep=token[1];
					System.out.println("setting dependency for load:"+" "+val);
					dependencies.set(val,true);
				}
				else 
					ex_dest_dep=null;
				execLoadStore();
				
				pipeline_array[cycle][2]=executeInstr;
			}
			else if(opcode.equalsIgnoreCase("SQUASH"))
			{
				pipeline_array[cycle][2]=executeInstr;
			}
		}
		else
		{	ex_dest_dep=null;
			pipeline_array[cycle][2]="NO-OP";
		}




		

	}

	private void ALU2() {                 //going forward with this stage only if ALU1 is done 

		if(isALU1){
			executeInstr=pipeline_array[cycle-1][2];
			String [] token=executeInstr.split(" ");
			String opcode=token[0];
			if(opcode.equalsIgnoreCase("ADD") || opcode.equalsIgnoreCase("SUB") || opcode.equalsIgnoreCase("MUL") || opcode.equalsIgnoreCase("DIV") || opcode.equalsIgnoreCase("AND")){
				pipeline_array[cycle][3]=executeInstr;
				//pipeline_array[cycle][3]=executeInstr;
			}
			else if(opcode.equalsIgnoreCase("MOV") || opcode.equalsIgnoreCase("MOVC")){
				//nothing to be performed here

				//stages.put("EXE", executeInstr);
				pipeline_array[cycle][3]=executeInstr;
				isExecute=true;
			}
			else if(opcode.equalsIgnoreCase("LOAD") || opcode.equalsIgnoreCase("STORE"))
			{
				//execLoadStore();
				pipeline_array[cycle][3]=executeInstr;
			}
			else if(opcode.equalsIgnoreCase("SQUASH") || opcode.equalsIgnoreCase("NO-OP"))
			{
				pipeline_array[cycle][3]=executeInstr;
			}
			

		}	
		System.out.println("execute instruction in ALU2 is:"+executeInstr);
	}

	private static void execarith()
	{
		//executeInstr=pipeline_array[cycle-1][2];
		Integer output=0;
		String [] token=executeInstr.split(" ");
		String opcode=token[0];
		System.out.println("");
		System.out.println("token[3] "+token[3]);
		Integer op1 = Integer.parseInt(token[2]);
		Integer op2 = Integer.parseInt(token[3]);
		System.out.println("opcode is:"+opcode);
		if(opcode.equalsIgnoreCase("ADD"))
		{
			System.out.println("*****************op1+op2 "+op1+" "+op2);
			//ex_dest_dep=token[1];
			output=op1 + op2;
			System.out.println("output of add"+output);
			if(output==0)
				
				zeroflag=true;
		}
		else if(opcode.equalsIgnoreCase("SUB"))
		{
			//ex_dest_dep=token[1];
			output=op1 - op2;
			System.out.println("output of sub"+output);
			if(output==0)
				zeroflag=true;
			System.out.println("zeroflag is:"+zeroflag);
		}
		else if(opcode=="MUL")
		{
			//ex_dest_dep=token[1];
			output=op1 * op2;
			if(output==0)
				zeroflag=true;
		}
		else if(opcode=="DIV")
		{
			//ex_dest_dep=token[1];
			output=op1 / op2;
			if(output==0)
				zeroflag=true;
		}
		else if(opcode=="AND")
		{
			//ex_dest_dep=token[1];
			output=op1 & op2;
			if(output==0)
				zeroflag=true;
		}
		else if(opcode=="OR")
		{
			//ex_dest_dep=token[1];
			output=op1 | op2;
			if(output==0)
				zeroflag=true;
		}
		else if(opcode=="XOR")
		{
			output=op1 ^ op2;
			//ex_dest_dep=token[1];
			if(output==0)
				zeroflag=true;
		}
		executeInstr=opcode+" "+token[1]+" "+output;

		isExecute=true;
		pipeline_array[cycle][2]=executeInstr;
	}

	private static void branch()
	{
		if(isBranchInstr && !stalled)
		{
			execBranch();
			if(BRANCH_TAKEN)
			{
				//ex_dest_dep=null;

				//pipeline_array[cycle][4]=executeInstr;    //made 4 from 2
				//String inst=pipeline_array[cycle][1];
				decodeInstr="SQUASH"+" "+decodeInstr;
				System.out.println("branch taken & instr in decode is:"+decodeInstr);
				//pipeline_array[cycle][1]=inst;
				//fetchedinstr=pipeline_array[cycle][0];
				fetchedinstr="SQUASH"+" "+fetchedinstr;
				pipeline_array[cycle][0]=fetchedinstr;
				pipeline_array[cycle][1]=decodeInstr;
				System.out.println("branch taken & instr in fetch is:"+fetchedinstr);
				//if(BRANCH_TAKEN)
				BRANCH_TAKEN = false;
				isBranchdone=true;
				stalled=false;
				fetchStalled=false;
			}
			System.out.println("execute instruction in branch is:"+executeInstr);
		}else
		{
		//ex_dest_dep=null;
		pipeline_array[cycle][4]="NO=OP";
		}

		//isBranchInstr=false;
	}
	private static void execBranch() {
		// TODO Auto-generated method stub
		System.out.println(pipeline_array[cycle-1][1]);
		executeInstr=pipeline_array[cycle-1][1];
		String[] token=executeInstr.split(" ");
		ex_dest_dep=null;
		if(token[0].equalsIgnoreCase("BZ") )
		{
			if(zeroflag) {
				int offset = Integer.parseInt(token[1]);
				//new_PC = (PC - 1) + offset;
				new_PC=PC+offset-2;     //set PC to start from 1 instead of 0
				BRANCH_TAKEN = true;
				PC=	Math.abs(new_PC);
				pipeline_array[cycle][4]=executeInstr;
				//	isBranchdone=true;
			}
		}
		if(token[0].equalsIgnoreCase("BNZ") )
		{
			System.out.println("zero flag in BNZ:"+zeroflag);
			if(!zeroflag) {
				Integer offset = Integer.parseInt(token[1]);
				//new_PC = (PC - 1) + offset;
				new_PC=PC+offset-2;  //where is PC set again to new_PC
				BRANCH_TAKEN = true;
				PC=	Math.abs(new_PC);
				pipeline_array[cycle][4]=executeInstr;
				//pipeline_array[cycle][5]=delay;
				//	isBranchdone=true;

			}
			System.out.println("in execBranch:"+executeInstr);
		}

		if(token[0].equalsIgnoreCase("BAL") || token[0].equalsIgnoreCase("JUMP"))
		{
			int reg=Integer.parseInt(token[1]);
			System.out.println("in jump execute"+token[2]);
			int literal=Integer.parseInt(token[2]);
			new_PC=reg+literal;
			new_PC=Math.abs(new_PC);
			//int pc1=PC-1;

			if(token[0].equalsIgnoreCase("BAL"))
			{
				executeInstr=""+" "+new_PC;

				pipeline_array[cycle][4]=executeInstr;
				//	pipeline_array[cycle][5]=delay;
				JUMPFLAG=true;
				BRANCH_TAKEN = true;
				//	isBranchdone=true;

			}
			else if(token[0].equalsIgnoreCase("JUMP"))
			{
				//PC=new_PC;

				executeInstr = token[0] + " " + new_PC;
				pipeline_array[cycle][4]=executeInstr;
				//pipeline_array[cycle][5]=delay;
				BRANCH_TAKEN = true;
				JUMPFLAG=true;
				//	isBranchdone=true;
			}

		}
	}

	private static void execLoadStore() {
		// TODO Auto-generated method stub
		lsflag=false;
		String[] token=executeInstr.split(" ");
		if(token[0].equalsIgnoreCase("STORE"))
		{
			int val1 = Integer.parseInt(token[2]);
			//System.out.println(src1);
			System.out.println("----\nval1 in store is:"+val1);
			int val2 = Integer.parseInt(token[3]);
			System.out.println("-----\nval2 in store is:"+val2);

			int dest = val1 + val2;
			

			executeInstr=token[0]+" "+token[1]+" "+dest;
			//pipeline_array[cycle][4]=executeInstr;
			//pipeline_array[cycle][5]=delay;
		}
		
		else if(token[0].equalsIgnoreCase("LOAD"))
		{

			int src1 = Integer.parseInt(token[2]);
			//System.out.println(src1);
			int src2 = Integer.parseInt(token[3]);
			//System.out.println(src2);

			int dest = src1 + src2;
			executeInstr=token[0]+" "+token[1]+" "+dest;
			//pipeline_array[cycle][4]=executeInstr;
			//pipeline_array[cycle][5]=delay;
		}
		isExecute=true;	
		lsflag=true;
		System.out.println("execute instr aftr execload:"+executeInstr);
	}

	void memory()
	{
		//memInstr=stages.get("EXE");
		if(cycle!=0)
		{
			if(pipeline_array[cycle-1][5]!="IDLE")
			{
				memInstr=pipeline_array[cycle-1][5];
				System.out.println("cycle is in wb"+cycle);
			}
			else if(pipeline_array[cycle-1][3]!="IDLE")
			{
				memInstr=pipeline_array[cycle-1][3];
				System.out.println("--------------cycle is in wb"+cycle);
			}else if(pipeline_array[cycle-1][5]=="IDLE" && pipeline_array[cycle-1][3]=="IDLE"){
				pipeline_array[cycle][6]="NO_OP";
				memInstr="NO-OP";
			}
		}
		else if(pipeline_array[cycle][5]!="IDLE")
		{
			memInstr=pipeline_array[cycle][5];
		}
		else if(pipeline_array[cycle][3]!="IDLE")
		{
			memInstr=pipeline_array[cycle][3];	
		}

		if(memInstr!=null){
			String [] token = memInstr.split(" ");
			String opcode = token[0];
			if(!token[0].equalsIgnoreCase("SQUASH"))
			{
				if(opcode.equalsIgnoreCase("ADD") || opcode.equalsIgnoreCase("SUB") || opcode.equalsIgnoreCase("MUL") || opcode.equalsIgnoreCase("DIV") || opcode.equalsIgnoreCase("AND"))
				{
					// Arithmetic operations, do nothing in memory stage
					//stages.put("MEM", memInstr);
					pipeline_array[cycle][6]=memInstr;
				}
				else if(opcode.equalsIgnoreCase("MOV") || opcode.equalsIgnoreCase("MOVC")){
					//nothing to be performed here

					//stages.put("MEM", memInstr);
					pipeline_array[cycle][6]=memInstr;
				}

				else if(opcode.equalsIgnoreCase("LOAD"))
				{
					int src = Integer.parseInt(token[2]);
					int output = Mem[src];
					memInstr=token[0]+" "+token[1]+" "+output;
					pipeline_array[cycle][3]=memInstr;
				}

				else if(opcode.equalsIgnoreCase("STORE"))
				{
					int src = Integer.parseInt(token[1]);
					int dest= Integer.parseInt(token[2]);
					Mem[dest] = src;
					//System.out.println("Memory for STORE: "+"memory["+destination+"]: "+memory[destination]);
					pipeline_array[cycle][6]=memInstr;
				}
				else if(opcode.equalsIgnoreCase("JUMP") || opcode.equalsIgnoreCase("BAL") || opcode.equalsIgnoreCase("BNZ") || opcode.equalsIgnoreCase("BZ"))
				{
					//execBranch();
					pipeline_array[cycle][6]=memInstr;
					System.out.println("in mem jump");
				}
				else{
					pipeline_array[cycle][6]="NO_OP";
				}
			}
			else{
				pipeline_array[cycle][6]="NO_OP";	//if meminstr is null
			}
		}	
	}


	void writeback()
	{
		//wbInstr=stages.get("MEM");
		if(cycle!=0)
		{
			wbInstr=pipeline_array[cycle-1][6];
			System.out.println("cycle is in wb"+cycle);
		}
		else
		{
			wbInstr=pipeline_array[cycle][6];
		}
		if(wbInstr!=null)
		{
			String[] token=wbInstr.split(" ");
			String dest;
			String opcode=token[0];

			if(token[0].equalsIgnoreCase("MOV")||token[0].equalsIgnoreCase("MOVC") )
			{
				dest=token[1];
				int val=Character.getNumericValue(token[1].charAt(1));
				if(!token[1].equals(ex_dest_dep))
				{
					dependencies.set(val, false);
				}
				registers.put(dest,Integer.parseInt(token[2]));
				System.out.println("in WB MOV:"+registers.get(dest));
				//	pipeline_array[cycle][4]=wbInstr;
				pipeline_array[cycle][7]=wbInstr;

			}
			else if(opcode.equalsIgnoreCase("ADD") || opcode.equalsIgnoreCase("SUB") || opcode.equalsIgnoreCase("MUL") || opcode.equalsIgnoreCase("DIV") || opcode.equalsIgnoreCase("AND"))
			{

				int val=Character.getNumericValue(token[1].charAt(1));
				if(!token[1].equals(ex_dest_dep))
				{
					dependencies.set(val, false);
				}
				dest=token[1];
				registers.put(dest, Integer.parseInt(token[2]));
				pipeline_array[cycle][7]=wbInstr;
			}
			else if(opcode.equalsIgnoreCase("BAL"))
			{
				dest=token[2];

				registers.put("X", Integer.parseInt(dest));
				pipeline_array[cycle][7]=wbInstr;
			}
			else if(opcode.equalsIgnoreCase("SQUASH") || opcode.equalsIgnoreCase("BZ") || opcode.equalsIgnoreCase("BNZ") || opcode.equalsIgnoreCase("JUMP") )
			{

				pipeline_array[cycle][7]=wbInstr;
			}
			else if(opcode.equalsIgnoreCase("STORE") || opcode.equalsIgnoreCase("LOAD") )
			{
				if(opcode.equalsIgnoreCase("LOAD")){
					int val=Character.getNumericValue(token[1].charAt(1));
					if(!token[1].equals(ex_dest_dep))
					{
						dependencies.set(val, false);
					}
				}
				pipeline_array[cycle][7]=wbInstr;
			}
		}

	}

	private void IntOp() {
		// TODO Auto-generated method stub



	}

	private void decodeArith() {
		// TODO Auto-generated method stub
		
		String[] token=decodeInstr.split(" ");
		System.out.println("indecode arith:"+decodeInstr);
		String src1=token[2];
		int s1=registers.get(src1);
		//int s2=registers.get(src1);
		String src2=token[3];
		int s2=registers.get(src2);
		decodeInstr=token[0]+" "+token[1]+" "+s1+" "+s2;
		//stages.put("D/RF", fetchedinstr);
		pipeline_array[cycle][1]=decodeInstr;
		//System.out.println(fetchedinstr);
		isDecode=true;
	}

	void display()
	{
		System.out.println("the contents of pipeline are:");
		System.out.println("THe size of cyclestatus is:"+cycleStatus.size());
		/*for(int i=0;i<printlist.size();i++){
		m=printlist.peek();
		System.out.println("contents of pipeline for cycle: "+i);*/
		for(int i=0;i<cycle_no;i++)
		{
			System.out.println("Iteration for cycle: "+i);
			for(int j=0;j<8;j++)
			{
				if(j==0)
					System.out.println("FETCH:\t\t"+pipeline_array[i][j]);
				if(j==1)
					System.out.println("DECODE:\t\t"+pipeline_array[i][j]);
				if(j==2)
					System.out.println("ALU1:\t\t"+pipeline_array[i][j]);
				if(j==3)
					System.out.println("ALU2:\t\t"+pipeline_array[i][j]);
				if(j==4)
					System.out.println("BRANCH:\t\t"+pipeline_array[i][j]);
				if(j==5)
					System.out.println("DELAY: \t\t"+pipeline_array[i][j]);
				if(j==6)
					System.out.println("MEMORY: \t"+pipeline_array[i][j]);
				if(j==7)
					System.out.println("WRITEBACK: \t"+pipeline_array[i][j]);
			}

			System.out.println(" ");
			/*System.out.println(m.get("F"));
			System.out.println(m.get("D/RF"));
			System.out.println(m.get("EX"));
			System.out.println(m.get("MEM"));
			System.out.println(m.get("WB"));*/
			
			System.out.println("\nThe memory contents are----------------------");
			for(int j=0;j<Mem.length;j++)
			{
				System.out.println(Mem[j]=-1);
			}
			
			System.out.println("\nThe register contents are-------------------");
			/*for(i=0;i<registers.size();i++)
			{*/
			System.out.println(registers.get("R0"));
			System.out.println(registers.get("R1"));
			System.out.println(registers.get("R2"));
			System.out.println(registers.get("R3"));
			System.out.println(registers.get("R4"));
			System.out.println(registers.get("R5"));
			System.out.println(registers.get("R6"));
			System.out.println(registers.get("R7"));
			System.out.println(registers.get("R8"));
			System.out.println(registers.get("R9"));
			System.out.println(registers.get("R10"));
			System.out.println(registers.get("R11"));
			System.out.println(registers.get("R12"));
			System.out.println(registers.get("R13"));
			System.out.println(registers.get("R14"));
			System.out.println(registers.get("R14"));
			System.out.println(registers.get("X"));
		//"	}
			
		}
	}	



	void exit()
	{
		System.exit(0);
	}

	private boolean checkDependency()
	{
		//System.out.println("In check Dependency");

		String check_dep = decodeInstr;
		//System.out.println(check);
	//	System.out.println("chk_dep"+check_dep);
		if(check_dep!=null)
		{
			String[] token = check_dep.split(" ");

			if
			(token[0].equals("ADD") || token[0].equals("SUB") || token[0].equals("MUL") 
					|| token[0].equals("AND") || token[0].equals("OR") || token[0].equals("EX-OR"))
			{
				//		System.out.println(splitCheck[0]);

				String src1 = token[2];
				int index1 = Character.getNumericValue(src1.charAt(1));
				//		System.out.println(index1);

				String src2 = token[3];
				int index2 = Character.getNumericValue(src2.charAt(1));
				//	System.out.println(index2);

				//	System.out.println(dependencies.get(index1));
				//	System.out.println(dependencies.get(index2));

				if(dependencies.get(index1) || dependencies.get(index2))
				{
					//		System.out.println(dependencies.get(index1));
					//	System.out.println(dependencies.get(index2));
					return true;
				}
			}

			else if(token[0].equals("MOV"))
			{

				String src1 = token[2];

				int index1;
				if(token[2].equals("X"))
				{
					return false;
				}

				index1 = Character.getNumericValue(src1.charAt(1));

				if(dependencies.get(index1))
				{
					return true;
				}
			}

			else if(token[0].equals("STORE"))
			{
				

				String src1 = token[1];
				int index1 = Character.getNumericValue(src1.charAt(1));


				String src2 = token[2];
				int index2 = Character.getNumericValue(src2.charAt(1));

				boolean storeFlag = false;


				int index3 = 0;

				if(registers.containsKey(token[3]))
				{
					String src3 = token[3];
					index3 = Character.getNumericValue(src3.charAt(1));
					//System.out.println(index3);
					storeFlag = true;
				}

				if(storeFlag)
				{
					if(dependencies.get(index1) || dependencies.get(index2) || dependencies.get(index3))
					{
						//		System.out.println(dependencies.get(index1));
						//	System.out.println(dependencies.get(index2));
						//System.out.println(dependencies.get(index3));

						return true;
					}
				}
				else
				{
					if(dependencies.get(index1) || dependencies.get(index2))
					{
						//System.out.println(dependencies.get(index1));
						//System.out.println(dependencies.get(index2));
						return true;
					}
				}



				if(dependencies.get(index1))
				{
					//	System.out.println(dependencies.get(index1));
					return true;
				}
			}

			else if(token[0].equals("LOAD"))
			{
				//System.out.println(splitCheck[0]);


				String src1 = token[2];
				int index1 = Character.getNumericValue(src1.charAt(1));
				//System.out.println(index1);

				boolean loadFlag = false;


				int index2 = 0;

				if(registers.containsKey(token[3]))
				{
					String src2 = token[3];
					index2 = Character.getNumericValue(src2.charAt(1));
					//	System.out.println(index2);
					loadFlag = true;
				}

				if(loadFlag)
				{
					if(dependencies.get(index1) || dependencies.get(index2))
					{
						//		System.out.println(dependencies.get(index1));
						//	System.out.println(dependencies.get(index2));
						return true;
					}
				}
				else
				{
					if(dependencies.get(index1))
					{
						//		System.out.println(dependencies.get(index1));
						return true;
					}
				}
			}	
			else if(token[0].equals("JUMP"))
			{
				//	System.out.println(splitCheck[0]);

				String src1 = token[1];
				int index1 = Character.getNumericValue(src1.charAt(1));

				if(dependencies.get(index1))
				{
					//		System.out.println(dependencies.get(index1));
					return true;
				}

			}
			else if(token[0].equals("BAL"))
			{
				//	System.out.println(splitCheck[0]);

				String src1 = token[1];
				int index1 = Character.getNumericValue(src1.charAt(1));

				if(dependencies.get(index1))
				{
					//		System.out.println(dependencies.get(index1));
					return true;
				}

			}
		}//if null

		return false;
	}



}