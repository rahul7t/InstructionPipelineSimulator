package pack1;
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
import pack1.*;
import sun.awt.AWTAccessor.SystemColorAccessor;
public class ApexPipeline {


	static Map<String, String> FRT= new HashMap<>();
	static Map<String, String> BRT= new HashMap<>();
	static Map<String, Integer> URF= new HashMap<>();
	//	 static LinkedList<List> ROB = new LinkedList<List>();
	static Queue<Integer> FreeList =new LinkedList<Integer>();
	//static Queue<String> IQ =new LinkedList<String>();
	int mulcycle=0;
	static int cycle_no=0;
	// IQ test1 = new IQ();
	static Integer[] Mem = new Integer[4000];
	static int PC=0,new_PC=0;
	static Map<String, Integer> registers = new HashMap<>();
	static Map<String, Integer> PhiReg = new HashMap<>();
	static ArrayList<String> InstructionList = new ArrayList<String>(PC);
	public static String[][] pipeline_array = new String[500][20];
	static String fetchedinstr,iqEntry;
	static boolean MULFU,isFetch,isDecode,isExecute,isMem,isWB,dispatch,lsflag,invalid_PC,fetchStalled,stalled,HALTFlag,loadflag,zeroflag,BRANCH_TAKEN,JUMPFLAG,isBranchInstr,isALU1,isBranchdone,isALU2done,isALU1free,isMulfree,isMULdone,ALUFU,m1done,m2done,m3done,m4done;
	static Queue<String> cycleStatus =new LinkedList<>();
	// static Queue<IQ> IssueQueue = new LinkedList<>();
	static ArrayList<Instruction> Iqueue=new ArrayList<Instruction>();
	static Map<String,String> stages=new HashMap<String,String>();
	static String decodeInstr1;
	static String decodeInstr2;
	static String executeInstr;
	static String memInstr,wbInstr,delay;
	static List<Boolean> dependencies=new ArrayList<Boolean>() ;
	static int cycle,s1bit=-1,s2bit=-1;
	static LinkedList<Map> printlist = new LinkedList<>();
	static String ex_dest_dep;
	static String decodestalled = null;
	static Map<String, Integer> dummy_Registers=new HashMap<>();
	static Integer output=0;
	Instruction DecodeObj1,exeobj,fetchobj,DecodeObj2,iqInstr,ALU1inst,ALU2Inst,ALUWBinst,mulInstrObj=null,M2obj=null,M3obj=null,M4obj=null,MulWBobj=null,lsobj1=null,lsobj2=null,ls_wbobj=null;
	// IQ issueQueueObj = null;
	static ArrayList<String> IQ=new ArrayList<String>();
	static ArrayList<Instruction> ROB=new ArrayList<Instruction>();
	// static ArrayList<String> ROB=new ArrayList<String>();
	//ROB rob=new ROB();	
	Instruction Inst_obj=null;
	void init(String filename)
	{
		PC=4000;
		mulcycle=0;
		cycle=0;
		//pipelinedInstr[0]=pipelinedInstr[1]="";
		isFetch=isDecode=isExecute=isMem=isWB=invalid_PC=lsflag=dispatch=fetchStalled=stalled=HALTFlag=zeroflag=loadflag=BRANCH_TAKEN=JUMPFLAG=isBranchInstr=isALU1=isBranchdone=isALU2done=isMulfree=isMULdone=false;
		isALU1free=true;
		ALUFU=false;
		MULFU=false;
		for(int i=0;i<Mem.length;i++)
		{
			Mem[i]=-1;
		}


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

		PhiReg.put("P0", 0);
		PhiReg.put("P1", 0);
		PhiReg.put("P2", 0);
		PhiReg.put("P3", 0);
		PhiReg.put("P4", 0);
		PhiReg.put("P5", 0);
		PhiReg.put("P6", 0);
		PhiReg.put("P7", 0);
		PhiReg.put("P8", 0);
		PhiReg.put("P9", 0);
		PhiReg.put("P10", 0);
		PhiReg.put("P11", 0);
		PhiReg.put("P12", 0);
		PhiReg.put("P13", 0);
		PhiReg.put("P14", 0);
		PhiReg.put("P15", 0);
		PhiReg.put("P16", 0);
		PhiReg.put("P17", 0);
		PhiReg.put("P18", 0);
		PhiReg.put("P19", 0);
		PhiReg.put("P20", 0);

		dummy_Registers.putAll(registers);

		for(int i = 0;i<500;i++)
			for(int j = 0;j<= 7;j++)
			{
				pipeline_array[i][j] = "IDLE";
			}

		ex_dest_dep=null;
		fetchStalled=false;
		for(int d=0;d<50;d++)
		{

			dependencies.add(false);
			Iqueue.add(d,null);
		}

		System.out.println("All registers,memory,stages and flags are initialized");

		prefetch(filename);

		for(int i=0;i<=20;i++) 
			FreeList.add(i);

		FRT.put("R0",null);
		FRT.put("R1",null);
		FRT.put("R2", null);
		FRT.put("R3", null);
		FRT.put("R4", null);
		FRT.put("R5", null);
		FRT.put("R6", null);
		FRT.put("R7", null);
		FRT.put("R8", null);
		FRT.put("R9", null);
		FRT.put("R10", null);
		FRT.put("R11", null);
		FRT.put("R12", null);
		FRT.put("R13", null);
		FRT.put("R14", null);
		FRT.put("R15", null);
		FRT.put("R16", null);
		FRT.putAll(BRT);

	}	

	void simulate(int no)
	{
		cycle_no=no;
		while(cycle<=no)
		{
			//			System.out.println("THe cycle is :"+cycle);
			//			System.out.println("simulate enter");
			//			System.out.println("fetch");
			//		writeback_MUL();
			//			writeback_Branch();
			//	writeback_LS();
			//	issueQueueObj= new IQ();
			System.out.println("Cycle is:"+cycle);
			Inst_obj=new Instruction();
			
			do_commit_ROB();
			LS_WB();      
			LSFU2();
			LSFU1();
			Mul_WB();
			Mul4();
			Mul3();
			Mul2();
			Mul1();
			
			
			writeback_ALU();

			//branch();

			ALU2();
			
			ALU1();
			//writeIQ_ROB();ormIQ();
			performIQ();
			decode2();
			decode1();

			fetch();
			//	Iqueue.add(cycle, Inst_obj);
			//stages.put("EX", inst);
			printlist.add(stages);
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



	void fetch()
	{
		if(!fetchStalled){



			if(PC>InstructionList.size()+4000)
			{
				invalid_PC=true;
			}
			else if(PC<(InstructionList.size()) && !BRANCH_TAKEN && !JUMPFLAG) {
				//				System.out.println("PC value is:"+PC+"instrn list size is:"+(InstructionList.size()));

				fetchedinstr=InstructionList.get(PC);
				Inst_obj.setInstruction(fetchedinstr);
				Inst_obj.setStr_rep_inst(fetchedinstr);
				fetchobj=Inst_obj;
				// Iqueue.add(cycle,Inst_obj);
				PC++;
				System.out.println("instrn fetched is:"+fetchedinstr);
				pipeline_array[cycle][0]=fetchedinstr; 
				System.out.println("instrn in pipeline 0 is :"+pipeline_array[cycle][0]);
			}

			//else
			/*{
			//not reading
			System.out.println("not reading as stalled");
			pipeline_array[cycle][0]=fetchedinstr+" stalled";
		}*/

			if(BRANCH_TAKEN || JUMPFLAG)
			{
				//			System.out.println("JUMFLAG In fetch:"+JUMPFLAG);
				fetchedinstr=InstructionList.get(PC);
				PC++;
				fetchedinstr="SQUASH"+" "+fetchedinstr;
				pipeline_array[cycle][0]=fetchedinstr;
				//pipeline_array[cycle][1]=decodeInstr1;
				//			System.out.println("branch taken & instr in fetch is:"+fetchedinstr);
				PC=new_PC;
				//			System.out.println("---PC value after jump\t:"+PC);


			}

			if(BRANCH_TAKEN)
				BRANCH_TAKEN=false;
			if(JUMPFLAG)
				JUMPFLAG=false;
			isFetch=true;
		}

	}


	void prefetch(String filename)
	{
		//		System.out.println("prefetch enter:");
		Scanner s;
		int counter=PC;
		try {
			s = new Scanner(new File(filename));

			for(int i = 0; i < counter; i++)
				InstructionList.add(i, null);

			while (s.hasNextLine())
			{
				String str = s.nextLine();
				InstructionList.add(counter++,(str.replaceAll("#", "").replaceAll(",", "")));
				System.out.println("the instructions is:"+str);
			}
			s.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}




	void decode1(){
		//		System.out.println("IN deocde stage");
		//	Instruction decodeIobj = Iqueue.get(cycle-1); 
		if(!stalled)
		{ 
			/*if(!isDecode)
			{*/
			fetchStalled=false;
			if(cycle!=0)
			{
				System.out.println("in decode if"+pipeline_array[cycle-1][0]);
				DecodeObj1=fetchobj  ; //fetchedinstr; 
				decodeInstr1=DecodeObj1.getInstruction();
				//pipeline_array[cycle-1][0];
				System.out.println("in decode 1 stage instr is:" +decodeInstr1);
				decodestalled=DecodeObj1.getInstruction();
			}
			else
			{

				decodeInstr1=null;//DecodeObj1.getInstruction();

			}

		}	
		else if(stalled)
		{
			decodeInstr1=decodestalled;
			//decodestalled ;

			//pipeline_array[cycle-2][0];
		}
		if(decodeInstr1!=null && !BRANCH_TAKEN && !JUMPFLAG)
		{

			//if(!checkDependency()){
			//	stalled=false;
			//				System.out.println("decodeInstr1: "+decodeInstr1);
			//issueQueueObj.setStatus(1);
			//stages.put("D/RF",decodeInstr1);
			String[] token=decodeInstr1.split(" ");
			//ReadFile record=new ReadFile(token[0],token[1],token[2]);
			String opcode=token[0];
			//Inst_obj.setFUType(opcode);
			if(opcode.equalsIgnoreCase("ADD") || opcode.equalsIgnoreCase("SUB") || opcode.equalsIgnoreCase("MUL") || opcode.equalsIgnoreCase("DIV") || opcode.equalsIgnoreCase("AND") || opcode.equalsIgnoreCase("OR"))
				decodeArith();
			else if(opcode.equalsIgnoreCase("MOV") || opcode.equalsIgnoreCase("MOVC"))
				decodeMov();
			else if(opcode.equalsIgnoreCase("LOAD") || opcode.equalsIgnoreCase("STORE")){
				decodeloadStore();
			}
			///	decodeloadStore();
			else if(opcode.equalsIgnoreCase("JUMP") || opcode.equalsIgnoreCase("BAL") || opcode.equalsIgnoreCase("BNZ") || opcode.equalsIgnoreCase("BZ")){

			}

			//decodeBranch();

			else if(opcode.equalsIgnoreCase("SQUASH")){
				pipeline_array[cycle][1]=decodeInstr1;

			}
			//}
			else if(opcode.equalsIgnoreCase("HALT"))
			{
				pipeline_array[cycle][1]=decodeInstr1+" "+"stalled";
				//stalled=true;
				fetchStalled=true;
			}
		}

		//}


		//		System.out.println("JUMPFLAG in decode:"+JUMPFLAG);

		if(BRANCH_TAKEN || JUMPFLAG)
		{
			//	pipeline_array[cycle][2]=executeInstr;
			//String inst=pipeline_array[cycle][1];
			decodeInstr1="SQUASH"+" "+decodeInstr1;
			//			System.out.println("branch taken & instr in decode is:"+decodeInstr1);
			pipeline_array[cycle][1]=decodeInstr1;

		}

	}
	private void decodeMov() {
		//		System.out.println("in movOp... ");
		// TODO Auto-generated method stub
		String[] token=decodeInstr1.split(" ");
		String opcode=token[0];
		int src1=0;
		//DecodeObj.setDest(token[1]);
		//String d1=FRT.get(token[1]);
		if(opcode.equalsIgnoreCase("MOVC"))
		{
			DecodeObj1.setFUType("ALU");
			System.out.println("decode 1 instr:"+decodeInstr1);
			pipeline_array[cycle][1]=decodeInstr1;
			System.out.println("decode 1 instr in pipiline:"+pipeline_array[cycle][1]);
			isDecode=true;
		}
		if(opcode.equalsIgnoreCase("MOV"))
		{
			//			System.out.println("In MOV code");
			if(token[2].charAt(0)=='R' || token[2].charAt(0)=='X' ){
				//src1 = dummy_Registers.get(token[2]);

			}
			else {
				//todo
				pipeline_array[cycle][1]=decodeInstr1;

			}



			//	decodeInstr1=opcode+" "+token[1]+" "+src1;

			//			System.out.println("exiting  MOV instruction:decoded instrn is"+decodeInstr1);	
			//stages.put("D/RF",decodeInstr1);				
			//pipeline_array[cycle][1]=decodeInstr1;
			isDecode=true;
		}
	}

	private void decodeloadStore() {
		// TODO Auto-generated method stub
		int src1;
		int src2,dest;
		String[] token=decodeInstr1.split(" ");
		String opcode=token[0];
		if(opcode.equalsIgnoreCase("LOAD"))
		{
			src1=dummy_Registers.get(token[2]);
				if(token[3].charAt(0)=='R' || token[3].charAt(0)=='X' ){
				dest = dummy_Registers.get(token[3]);
				loadflag=true;
			}
			else {
				dest = Integer.parseInt(token[3]);
			}
			decodeInstr1=opcode+" "+token[1]+" "+src1+" "+dest;
			pipeline_array[cycle][1]=decodeInstr1;
			isDecode=true;
			//lsflag=true;
		}
		if(opcode.equalsIgnoreCase("STORE"))
		{
			src1=dummy_Registers.get(token[1]);
			if(token[3].charAt(0)=='R' || token[3].charAt(0)=='X' ){
				dest = registers.get(token[3]);
			}
			else {
				dest = Integer.parseInt(token[3]);
			}
			//dest=Integer.parseInt(token[1]);
			src2=dummy_Registers.get(token[2]);
			decodeInstr1=opcode+" "+src1+" "+src2+" "+dest;
			pipeline_array[cycle][1]=decodeInstr1;
			isDecode=true;
			//	lsflag=true;
		}
		///lasttwoInstr[0]
	}

	void decode2()
	{
		if(isDecode){

			DecodeObj2=DecodeObj1;
			decodeInstr2=DecodeObj2.getInstruction();//pipeline_array[cycle-1][1];   //fetchedinstr; 


			String[] token=decodeInstr2.split(" ");

			String opcode=token
					[0];
			String dest;
			int d,s1,s2;
			if(opcode.equalsIgnoreCase("ADD") || opcode.equalsIgnoreCase("SUB") || opcode.equalsIgnoreCase("MUL") || opcode.equalsIgnoreCase("DIV") || opcode.equalsIgnoreCase("AND") || opcode.equalsIgnoreCase("OR"))
			{
				d=FreeList.poll();
				dest="P"+d;
				dependencies.set(d, true);
				FRT.put(token[1],dest);
				decodeInstr2=token[0]+" "+dest+ " "+token[2]+" "+token[3];
				DecodeObj2.setInstruction(decodeInstr2);
				/*if(token[2].charAt(0)=='P' || token[3].charAt(0)=='P' )
			{
				if(token[2].charAt(0)=='P' && token[3].charAt(0)=='P')
				decodeInstr2=token[0]+" "+dest+" "+token[2]+" "+token[3];


				if(token[2].charAt(0)=='P' && token[3].charAt(0)!='P')
				{

					decodeInstr2=token[0]+" "+dest+" "+token[2]+" "+token[3];
				}
			}
				 */


				/*int index1 = Character.getNumericValue(token[2].charAt(1));
			int index2 = Character.getNumericValue(token[3].charAt(1));
			if(!dependencies.get(index1)){
				System.out.println("token[2]:"+token[2]);
			 s1=PhiReg.get(token[2]);
			 decodeInstr2=decodeInstr2+" "+s1;
			 s1bit=1;
			}
			else{
			decodeInstr2=decodeInstr2+" "+token[2];
			s1bit=0;
			}

			if(!dependencies.get(index2)){
			 s2=PhiReg.get(token[3]);
			 decodeInstr2=decodeInstr2+" "+s2;
			 s2bit=1;
			}
			else{
				decodeInstr2=decodeInstr2+" "+token[3];
				s2bit=0;
			}*/

				pipeline_array[cycle][2]=decodeInstr2;
				System.out.println("indecode 2 instr is:"+pipeline_array[cycle][2]);
				Iqueue.add(DecodeObj2);
			}

			else if(opcode.equalsIgnoreCase("MOV") || opcode.equalsIgnoreCase("MOVC"))
			{
				d=FreeList.poll();
				dest="P"+d;
				FRT.put(token[1],dest);
				dependencies.set(d, true);
				DecodeObj2.setDest(dest);
				if(opcode.equalsIgnoreCase("MOVC")){			
					decodeInstr2=token[0]+" "+dest+" "+token[2];
					DecodeObj2.setInstruction(decodeInstr2);
					//	issueQueueObj.setDest(dest);  //set in IQ
					//issueQueueObj.setFUType("ALU");
					pipeline_array[cycle][2]=decodeInstr2;
					System.out.println("Decode2 instruction "+decodeInstr2);
					Iqueue.add(DecodeObj2);
				}
			}
			else if(opcode.equalsIgnoreCase("LOAD") || opcode.equalsIgnoreCase("STORE"))
			{
				//todo
			}
			else if(opcode.equalsIgnoreCase("JUMP") || opcode.equalsIgnoreCase("BAL") || opcode.equalsIgnoreCase("BNZ") || opcode.equalsIgnoreCase("BZ"))
				//todo{
			{

			}
			else if(opcode.equalsIgnoreCase("SQUASH"))
				pipeline_array[cycle][2]=decodeInstr2;


			//writeIQ_ROB();
		}
	}

	/*void writeIQ_ROB()
	{
		if(cycle>=3){
		//iqEntry=decodeInstr2;
		System.out.println("iqEntry:"+decodeInstr2);
		if(!decodeInstr2.equalsIgnoreCase("IDLE")||decodeInstr2!=null ){
		Iqueue.add(DecodeObj2);

		}
		}
	}*/

	void performIQ()
	{
		if(!Iqueue.isEmpty()){
			for(int i=0;i<Iqueue.size();i++)
			{
				if(Iqueue.get(i)!=null){
					iqInstr=Iqueue.get(i);
					System.out.println("iqinst:"+iqInstr);
					/*String[] token=iqInstr.split(" ");
				String [] subtoken=token[1].split(" ");*/
					iqEntry=iqInstr.getInstruction();
					String[] token=iqEntry.split(" ");
					System.out.println("token is  "+token[0]);
					if(token[0].equalsIgnoreCase("MOVC")){
						//	ALU1();
						ALUFU=true;
						Iqueue.remove(i);
					}
					if(token[0].equalsIgnoreCase("ADD") || token[0].equalsIgnoreCase("SUB") || token[0].equalsIgnoreCase("DIV"))
					{
						if(iqInstr.getSrc1ReadyBit()!=0 && iqInstr.getSrc2ReadyBit()!=0)
						{
							ALUFU=true;
							Iqueue.remove(i);
						}
						else
							ALUFU=false;
					}
					if(token[0].equalsIgnoreCase("MUL"))
					{
						if(iqInstr.getSrc1ReadyBit()!=0 && iqInstr.getSrc2ReadyBit()!=0)
						{
							MULFU=true;
							Iqueue.remove(i);
							iqInstr.setMulcycle(0);
							
						}
						else
							MULFU=false;
					
					}
				}
				else
				{
					iqInstr=null;
				}






			}
		}
	}









	void ALU1()
	{
		ALU1inst=iqInstr;
		//		System.out.println("cycle is:"+cycle+" isDecode: "+isDecode+" isBranchInstr"+isBranchInstr+" stalled:"+stalled);
		if(ALUFU && ALU1inst!=null)
		{ 
			System.out.println("in ALU1");
			isALU1=true;

			executeInstr=ALU1inst.getInstruction();

			String [] token=executeInstr.split(" ");
			String opcode=token[0];

			if(opcode.equalsIgnoreCase("ADD") || opcode.equalsIgnoreCase("SUB") || opcode.equalsIgnoreCase("DIV") || opcode.equalsIgnoreCase("AND")){

				ex_dest_dep=token[1];
				//int val=Character.getNumericValue(token[1].charAt(1));
				//dependencies.set(val,true);

				execarith();

			}

			else if(opcode.equalsIgnoreCase("MOV") || opcode.equalsIgnoreCase("MOVC")){

				ex_dest_dep=token[1];
				int val=Character.getNumericValue(token[1].charAt(1));
				System.out.println("vals:"+val);
				dependencies.set(val,true);
				pipeline_array[cycle][3]=executeInstr;
				isExecute=true;
			}


			else if(opcode.equalsIgnoreCase("SQUASH"))
			{
				pipeline_array[cycle][3]=executeInstr;
			}
			isALU1free=true;
		}
		else
		{	ex_dest_dep=null;
		pipeline_array[cycle][3]="NO-OP";
		isALU1=false;
		}
		//		ALU2();
	}


	private  void execarith()
	{
		String [] token=executeInstr.split(" ");
		String opcode=token[0];
		System.out.println();
		Integer op1 = Integer.parseInt(token[2]);
		Integer op2 = Integer.parseInt(token[3]);
		if(opcode.equalsIgnoreCase("ADD"))
		{

			output=op1 + op2;
			//PhiReg.put(token[1], output);
			/*if(output==0)

				zeroflag=true;*/
		}
		else if(opcode.equalsIgnoreCase("SUB"))
		{
			output=op1 - op2;
			//PhiReg.put(token[1], output);
			/*if(output==0)
				zeroflag=true;*/
		}

		else if(opcode=="DIV")
		{
			//ex_dest_dep=token[1];
			output=op1 / op2;
			//PhiReg.put(token[1], output);
			/*if(output==0)
				zeroflag=true;*/
		}
		else if(opcode=="AND")
		{
			//ex_dest_dep=token[1];
			output=op1 & op2;
			//PhiReg.put(token[1], output);
			/*if(output==0)
				zeroflag=true;*/
		}
		else if(opcode=="OR")
		{
			//ex_dest_dep=token[1];
			output=op1 | op2;
			//PhiReg.put(token[1], output);
			/*if(output==0)
				zeroflag=true;*/
		}
		else if(opcode=="XOR")
		{
			output=op1 ^ op2;
			//PhiReg.put(token[1], output);
			//ex_dest_dep=token[1];
			/*if(output==0)
				zeroflag=true;*/
		}
		executeInstr=opcode+" "+token[1]+" "+output;

		isExecute=true;
		pipeline_array[cycle][3]=executeInstr;
		ALU1inst.setInstruction(executeInstr);
	}




	private void ALU2() {

		if(isALU1){
			//executeInstr=pipeline_array[cycle-1][3];
			ALU2Inst=ALU1inst;
			executeInstr=ALU1inst.getInstruction();
			String [] token=executeInstr.split(" ");
			String opcode=token[0];

			if(opcode.equalsIgnoreCase("ADD") || opcode.equalsIgnoreCase("SUB") || opcode.equalsIgnoreCase("DIV") || opcode.equalsIgnoreCase("AND")){
				pipeline_array[cycle][4]=executeInstr;
				if(Integer.parseInt(token[2])==0){
					//pipeline_array[cycle][3]=executeInstr;
					//dummy_Registers.put(token[1], output);
					/*int val=Character.getNumericValue(token[1].charAt(1));
				dependencies.set(val, false);*/
					zeroflag=true;
				}
			}
			else if(opcode.equalsIgnoreCase("MOV") || opcode.equalsIgnoreCase("MOVC")){
				//nothing to be performed here

				//stages.put("EXE", executeInstr);
				pipeline_array[cycle][4]=executeInstr;
				isExecute=true;
			}

			else if(opcode.equalsIgnoreCase("SQUASH") || opcode.equalsIgnoreCase("NO-OP"))
			{
				pipeline_array[cycle][4]=executeInstr;
			}

			isALU2done=true;
		}	

		//		System.out.println("execute instruction in ALU2 is:"+executeInstr);
	}





	void writeback_ALU()
	{


		if(isALU2done){
			/*if(cycle!=0)
		{
			ALUWBinst=ALU2Inst;
			wbInstr=ALUWBinst.getInstruction();
		}
		else
		{
			ALUWBinst=ALU2Inst;
			wbInstr=ALUWBinst.getInstruction();		}*/
			ALUWBinst=ALU2Inst;
			wbInstr=ALUWBinst.getInstruction();
			if(wbInstr!=null)
			{
				String[] token=wbInstr.split(" ");
				String dest=null;
				String opcode=token[0];

				if(token[0].equalsIgnoreCase("MOV")||token[0].equalsIgnoreCase("MOVC") )
				{
					dest=token[1];
					int val=Character.getNumericValue(token[1].charAt(1));
					/*if(!token[1].equals(ex_dest_dep))
				{*/
					dependencies.set(val, false);
					System.out.println("dependency set to false for:"+wbInstr);
					//}
					//	registers.put(dest,Integer.parseInt(token[2]));
					//	dummy_Registers.put(dest, Integer.parseInt(token[2]));*/
					PhiReg.put(token[1], Integer.parseInt(token[2]));
					pipeline_array[cycle][5]=wbInstr;
					ALUWBinst.setDestValue(token[2]);

				}
				else if(opcode.equalsIgnoreCase("ADD") || opcode.equalsIgnoreCase("SUB") || opcode.equalsIgnoreCase("DIV") || opcode.equalsIgnoreCase("AND"))
				{
					dest=token[1];

					int val=Character.getNumericValue(token[1].charAt(1));
					dependencies.set(val, false);
					/*dest=token[1];
				registers.put(dest, Integer.parseInt(token[2]));*/
					PhiReg.put(token[1], Integer.parseInt(token[2]));
					pipeline_array[cycle][5]=wbInstr;
					ALUFU=false;
				}


				//				pipeline_array[cycle][7]=wbInstr;
				
				/*String[] fwdstr=decodeInstr2.split(" ");
				System.out.println("decodeinstr2 is :"+decodeInstr2);
				if(!fwdstr[0].equalsIgnoreCase("MOVC")) 
				{
						if( (dest.equals(fwdstr[2]) || dest.equals(fwdstr[3])))
						{ 
							int s1,s2;
					if(dest.equals(fwdstr[2]) && dest.equals(fwdstr[3]))
					{
						s1=PhiReg.get(dest);
						s2=PhiReg.get(dest);
						decodeInstr2=fwdstr[0]+" "+fwdstr[1]+" "+s1+" "+s2;
						DecodeObj2.setInstruction(decodeInstr2);
					}
					else if(dest.equals(fwdstr[2]) && !dest.equals(fwdstr[3]))
					{
						s1=PhiReg.get(dest);
						
						decodeInstr2=fwdstr[0]+" "+fwdstr[1]+" "+s1+" "+fwdstr[3];
						DecodeObj2.setInstruction(decodeInstr2);
					}
					else if(!dest.equals(fwdstr[2]) && dest.equals(fwdstr[3]))
							{
							s2=PhiReg.get(dest);
							decodeInstr2=fwdstr[0]+" "+fwdstr[1]+" "+fwdstr[2]+" "+s2;
						DecodeObj2.setInstruction(decodeInstr2);
							}
				}
				}*/
				/*for(int i=0;i<40;i++)
				{
					if(Iqueue.get(i)!=null){
						Instruction tempobj=new Instruction();
						tempobj=Iqueue.get(i);
						if(tempobj.getFUType()!="MOVC"){

							if(dest.equals(tempobj.getSrc1())|| dest.equals(tempobj.getSrc2())){

								if(dest.equals(tempobj.getSrc1()))
								{
									tempobj.setSrc1ReadyBit(1);
									tempobj.setSrc1Value(Integer.parseInt(dest));
									String[] ist=tempobj.getInstruction().split(" ");
									ist[2]=dest;
									tempobj.setInstruction(ist.toString() );
									System.out.println("inst for tempobj for src1 :"+tempobj.getInstruction());

								}
								if(dest.equals(tempobj.getSrc2()))
								{
									tempobj.setSrc2ReadyBit(1);
									tempobj.setSrc2Value(Integer.parseInt(dest));
									String[] ist=tempobj.getInstruction().split(" ");
									ist[3]=dest;
									tempobj.setInstruction(ist.toString() );
									System.out.println("inst for tempobj for src2 :"+tempobj.getInstruction());

								}
								Iqueue.add(i,tempobj);
							}
						}
					}*/
				dofwd(dest);
				}
			isALU2done=false;
			}
			
		
		else{
			pipeline_array[cycle][5]="NO-OP";
		}

	}
	
	
	
	void dofwd(String dest)
	{
		String[] fwdstr=decodeInstr1.split(" ");
		System.out.println("decodeInstr1 is :"+decodeInstr1+" dest is:"+dest);
		if(fwdstr[0].equalsIgnoreCase("ADD") ||fwdstr[0].equalsIgnoreCase("SUB") || fwdstr[0].equalsIgnoreCase("MUL") || fwdstr[0].equalsIgnoreCase("DIV")) 
		{
			
				if(dest.equals(fwdstr[2]) || dest.equals(fwdstr[3]))
				{ 
					int s1,s2;
			if(dest.equals(fwdstr[2]) && dest.equals(fwdstr[3]))
			{
				s1=PhiReg.get(dest);
				s2=PhiReg.get(dest);
				decodeInstr1=fwdstr[0]+" "+fwdstr[1]+" "+s1+" "+s2;
				DecodeObj1.setInstruction(decodeInstr1);
				DecodeObj1.setSrc1ReadyBit(1);
				DecodeObj1.setSrc2ReadyBit(1);
			}
			else if(dest.equals(fwdstr[2]) && !dest.equals(fwdstr[3]))
			{
				System.out.println("matching token 2"+fwdstr[2]);
				s1=PhiReg.get(dest);
				
				decodeInstr1=fwdstr[0]+" "+fwdstr[1]+" "+s1+" "+fwdstr[3];
				DecodeObj1.setInstruction(decodeInstr1);
				DecodeObj1.setSrc1ReadyBit(1);
			}
			else if(!dest.equals(fwdstr[2]) && dest.equals(fwdstr[3]))
					{
					s2=PhiReg.get(dest);
					decodeInstr1=fwdstr[0]+" "+fwdstr[1]+" "+fwdstr[2]+" "+s2;
					DecodeObj1.setInstruction(decodeInstr1);
					DecodeObj1.setSrc2ReadyBit(1);
					}
		}
		}
		for(int i=0;i<40;i++)
		{
			if(Iqueue.get(i)!=null){
				Instruction tempobj=new Instruction();
				tempobj=Iqueue.get(i);
				if(tempobj.getFUType()!="MOVC"){

					if(dest.equals(tempobj.getSrc1())|| dest.equals(tempobj.getSrc2())){

						if(dest.equals(tempobj.getSrc1()))
						{
							tempobj.setSrc1ReadyBit(1);
							tempobj.setSrc1Value(Integer.parseInt(dest));
							String[] ist=tempobj.getInstruction().split(" ");
							ist[2]=dest;
							tempobj.setInstruction(ist.toString() );
							System.out.println("inst for tempobj for src1 :"+tempobj.getInstruction());

						}
						if(dest.equals(tempobj.getSrc2()))
						{
							tempobj.setSrc2ReadyBit(1);
							tempobj.setSrc2Value(Integer.parseInt(dest));
							String[] ist=tempobj.getInstruction().split(" ");
							ist[3]=dest;
							tempobj.setInstruction(ist.toString() );
							System.out.println("inst for tempobj for src2 :"+tempobj.getInstruction());

						}
						Iqueue.add(i,tempobj);
					}
				}
			}
	}
}

	void Mul1()
	{
		if(cycle>5 && iqInstr!=null){
		
		//mulInstrObj=iqInstr;
		String[] token=iqInstr.getInstruction().split(" ");
		System.out.println("in mul1:"+iqInstr.getInstruction());
		if(token[0].equalsIgnoreCase("MUL") && iqInstr.getMulcycle()==0){
			mulInstrObj=iqInstr;
		
		int op1=Integer.parseInt(token[2]);
		int op2=Integer.parseInt(token[3]);
		String mulInstr=token[0]+" "+token[1]+" "+op1*op2;
		pipeline_array[cycle][6]=mulInstr;
		mulInstrObj.setInstruction(mulInstr);
		mulInstrObj.setMulcycle(1);
	}
	}
	}
	void Mul2()
	{
		
		M2obj=mulInstrObj;
		if(cycle>6 && M2obj!=null){
			String [] token=M2obj.getInstruction().split(" ");
			if(token[0].equalsIgnoreCase("MUL") && M2obj.getMulcycle()==1 ){
	//	if(pipeline_array[cycle-1][6]!=null)
			pipeline_array[cycle][7]=pipeline_array[cycle-1][6];
			M2obj.setMulcycle(2);
			}
	//	mulcycle=2;
		}
	}
	void Mul3()
	{		
		
		M3obj=M2obj;
		if(cycle>7 && M3obj!=null){
			String [] token=M3obj.getInstruction().split(" ");
			if(token[0].equalsIgnoreCase("MUL") && M3obj.getMulcycle()==2 ){
	//	if(pipeline_array[cycle-1][6]!=null)
			pipeline_array[cycle][8]=pipeline_array[cycle-1][7];
			M3obj.setMulcycle(3);
		
			}
		}
	}
	void Mul4()
	{
		
		M4obj=M3obj;
		if(cycle>8 && M4obj!=null){
			String [] token=M4obj.getInstruction().split(" ");
			if(token[0].equalsIgnoreCase("MUL") && M4obj.getMulcycle()==3 ){
	//	if(pipeline_array[cycle-1][6]!=null)
			pipeline_array[cycle][9]=pipeline_array[cycle-1][8];
			M4obj.setMulcycle(4);
		
			}
		}
	
		
		}

	void Mul_WB()
	{
		MulWBobj=M4obj;
		if(cycle>9 && MulWBobj!=null){
		String mulwbinst="";
		
		mulwbinst = MulWBobj.getInstruction();
		String[] token=mulwbinst.split(" ");
		
		System.out.println("mul instr:"+mulwbinst);
		System.out.println("pipeline_array[cycle][9]:"+pipeline_array[cycle][9]);
		//String [] pipeInst=pipeline_array[cycle-1][9].split(" ");
		
		if(token[0].equalsIgnoreCase("MUL" ) && MulWBobj.mulcycle==4)
		{
			
			//Strtoken=mulwbinst.split(" ");
			String dest=token[1];
			PhiReg.put(token[1], Integer.parseInt(token[2]));
			System.out.println("the wb value of mul is:"+token[2]);
			int val=Character.getNumericValue(token[1].charAt(1));
			pipeline_array[cycle][10]=pipeline_array[cycle-1][9] + "WB";
			dependencies.set(val, false);
			dofwd(dest);
		//	Mul4();	
		}
		
	}
		}


void LSFU1()
{
	Instruction lsobj1=iqInstr;
	String[] inst=iqInstr.getInstruction().split(" ");
	if(inst[0].equalsIgnoreCase("LOAD") && cycle>4)
	{
		int src = Integer.parseInt(inst[2]);
		int output = Mem[src];
		/*int val=Character.getNumericValue(token[1].charAt(1));
		dummy_Registers.put(token[1], output);
		dependencies.set(val, false);*/
		memInstr=inst[0]+" "+inst[1]+" "+output;
		pipeline_array[cycle][3]=memInstr;
		PhiReg.put(inst[1], output);
	}

	else if(inst[0].equalsIgnoreCase("STORE") && cycle>4)
	{
		int src = Integer.parseInt(inst[1]);
		int dest= Integer.parseInt(inst[2]);
		//Mem[dest] = src;
		//System.out.println("Memory for STORE: "+"memory["+destination+"]: "+memory[destination]);
		System.out.println("STORE MEM UPDATE"+Mem[dest]+" src :"+src+": dest: "+dest);
		pipeline_array[cycle][12]=memInstr;
	}
	lsobj1.setInstruction(memInstr);
}

void LSFU2()
{
	if(cycle>5)
	{
		if(lsobj1!=null)
		{
			lsobj2=lsobj1;
			String[] inst=lsobj2.getInstruction().split(" ");
			if(inst[0].equalsIgnoreCase("LOAD") )
			{
				pipeline_array[cycle][13]=	lsobj2.getInstruction();
			}
			if(inst[0].equalsIgnoreCase("STOER") )
			{
				pipeline_array[cycle][13]=	lsobj2.getInstruction();
			}
			
	
}
	}
	
}

void LS_WB()
{
	if(lsobj2!=null)
	{
		ls_wbobj=lsobj2;
		String[] inst=lsobj2.getInstruction().split(" ");
		if(inst[0].equalsIgnoreCase("LOAD") )
		{
			PhiReg.put(inst[1], output);	
		}
		if(inst[0].equalsIgnoreCase("STORE"))
		{
			int src = Integer.parseInt(inst[1]);
			int dest= Integer.parseInt(inst[2]);
			Mem[dest] = src;
		}
}
}

void do_commit_ROB()
{
	Instruction robintobj;
	if(!ROB.isEmpty())
	{
		
			robintobj=ROB.get(0);
			if(robintobj.getStatus()==1)
			{
				BRT.put(robintobj.dest, robintobj.destValue);
			}
			
		
}
}

	private void decodeArith() {
		// TODO Auto-generated method stub
		int sr1=-1,sr2=-1;
		String[] token=decodeInstr1.split(" ");
		System.out.println("indecode arith:"+decodeInstr1);
		//String src1=token[2];
		String s1=FRT.get(token[2]);
		String s2=FRT.get(token[3]);

		int index1 = Character.getNumericValue(s1.charAt(1));
		int index2 = Character.getNumericValue(s2.charAt(1));
		if(dependencies.get(index1))
		{
			DecodeObj1.setSrc1ReadyBit(0);
		}
		else if(!dependencies.get(index1))
		{
			DecodeObj1.setSrc1ReadyBit(1);
			sr1=PhiReg.get(s1);
			//decodeInstr1=token[0]+" "+token[1]+" "+sr1;
		}
		if(dependencies.get(index2))
		{
			DecodeObj1.setSrc2ReadyBit(0);
		}
		else if(!dependencies.get(index2))
		{
			DecodeObj1.setSrc2ReadyBit(1);
			sr2=PhiReg.get(s2);
		}
		if(sr1!=-1 && sr2!=-1){
			decodeInstr1=token[0]+" "+token[1]+" "+sr1+" "+sr2;
		}
		if(sr1==-1 && sr2==-1)
			decodeInstr1=token[0]+" "+token[1]+" "+s1+" "+s2;
		if(sr1==-1 && sr2!=-1)
			decodeInstr1=token[0]+" "+token[1]+" "+s1+" "+sr2;
		if(sr1!=-1 && sr2==-1)
			decodeInstr1=token[0]+" "+token[1]+" "+sr1+" "+s2;			

		DecodeObj1.setInstruction(decodeInstr1);
		System.out.println("instr after decoding arith:"+decodeInstr1);
		//stages.put("D/RF", fetchedinstr);
		pipeline_array[cycle][1]=decodeInstr1;

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
			for(int j=0;j<11;j++)
			{
				if(j==0)
					System.out.println("FETCH:\t\t"+pipeline_array[i][j]);
				if(j==1)
					System.out.println("DECODE1:\t\t"+pipeline_array[i][j]);
				if(j==2)
					System.out.println("DECODE2:\t\t"+pipeline_array[i][j]);
				if(j==3)
					System.out.println("ALU1:\t\t"+pipeline_array[i][j]);
				if(j==4)
					System.out.println("ALU2:\t\t"+pipeline_array[i][j]);
				//					System.out.println("BRANCH:\t\t"+pipeline_array[i][j]);
				if(j==5)
					System.out.println("ALU_WB:\t\t"+pipeline_array[i][j]);
				//					System.out.println("DELAY: \t\t"+pipeline_array[i][j]);
				if(j==6)
					System.out.println("MUL1:\t\t"+pipeline_array[i][j]);
//					System.out.println("MEMORY: \t"+pipeline_array[i][j]);
				if(j==7)
					System.out.println("MUL2:\t\t"+pipeline_array[i][j]);
//					System.out.println("WRITEBACK: \t"+pipeline_array[i][j]);
				if(j==8)
					System.out.println("MUL3:\t\t"+pipeline_array[i][j]);
				if(j==9)
					System.out.println("MUL4:\t\t"+pipeline_array[i][j]);
				if(j==10)
					System.out.println("MUL_WB:\t\t"+pipeline_array[i][j]);
			}

			System.out.println(" ");


		}

		System.out.println("\nThe memory contents are----------------------");
		for(int j=0;j<Mem.length;j++)
		{

			System.out.print("\t Mem["+j+"]: "+Mem[j]);

		}

		System.out.println("\nThe register contents are-------------------");

		System.out.println("P0	"+PhiReg.get("P0"));
		System.out.println("P1	"+PhiReg.get("P1"));
		System.out.println("P2	"+PhiReg.get("P2"));
		System.out.println("P3	"+PhiReg.get("P3"));
		System.out.println("P4	"+PhiReg.get("P4"));
		System.out.println("P5	"+PhiReg.get("P5"));
		System.out.println("P6	"+PhiReg.get("P6"));
		System.out.println("P7	"+PhiReg.get("P7"));
		System.out.println("P8	"+PhiReg.get("P8"));
		System.out.println("P9	"+PhiReg.get("P9"));
		System.out.println("P10	"+PhiReg.get("P10"));
		System.out.println("P11	"+PhiReg.get("P11"));
		System.out.println("P12	"+PhiReg.get("P12"));
		System.out.println("P13	"+PhiReg.get("P13"));
		System.out.println("P14	"+PhiReg.get("P14"));
		System.out.println("P15	"+PhiReg.get("P15"));
		System.out.println("X	"+PhiReg.get("X"));

		System.out.println("The contents of FRT table:");

		System.out.println(FRT.values());
		/*FRT.put("R1",null);
		FRT.put("R2", null);
		FRT.put("R3", null);
		FRT.put("R4", null);
		FRT.put("R5", null);
		FRT.put("R6", null);
		FRT.put("R7", null);
		FRT.put("R8", null);
		FRT.put("R9", null);
		FRT.put("R10", null);
		FRT.put("R11", null);
		FRT.put("R12", null);
		FRT.put("R13", null);
		FRT.put("R14", null);
		FRT.put("R15", null);
		FRT.put("R16", null);*/
	}




	void exit()
	{
		System.exit(0);
	}

	private boolean checkDependency()
	{
		//System.out.println("In check Dependency");

		String check_dep = decodeInstr1;
		//System.out.println(check);
		//System.out.println("chk_dep"+check_dep);
		if(check_dep!=null)
		{
			String[] token = check_dep.split(" ");

			if(token[0].equals("ADD") || token[0].equals("SUB") || token[0].equals("MUL") 
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
				/*String dest=token[1];
				int index1 = Character.getNumericValue(dest.charAt(1));*/


				if(dependencies.get(index1)|| dependencies.get(index2))
				{
					//		System.out.println(dependencies.get(index1));
					//	System.out.println(dependencies.get(index2));
					return true;
				}
			}

			else if(token[0].equals("MOV"))
			{
				//System.out.println(splitCheck[0]);

				String src1 = token[2];

				int index1;
				if(token[2].equals("X"))
				{
					return false;
				}

				index1 = Character.getNumericValue(src1.charAt(1));
				//System.out.println(index1);

				if(dependencies.get(index1))
				{
					return true;
				}
			}

			else if(token[0].equals("STORE"))
			{
				//	System.out.println(splitCheck[0]);

				String src1 = token[1];
				int index1 = Character.getNumericValue(src1.charAt(1));


				String src2 = token[2];
				int index2 = Character.getNumericValue(src2.charAt(1));
				//System.out.println(index2);

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
	
	
void Print_map_tables(){
	System.out.println("The contents of Front Rename Table:");
	//System.out.println(FRT. values());
	System.out.println("Reg\tValue");
	System.out.println("R1\t"+FRT.get("R1"));
	System.out.println("R2\t"+FRT.get("R2"));
	System.out.println("R3\t"+FRT.get("R3"));
	System.out.println("R4\t"+FRT.get("R4"));
	System.out.println("R5\t"+FRT.get("R5"));
	System.out.println("R6\t"+FRT.get("R6"));
	System.out.println("R7\t"+FRT.get("R7"));
	System.out.println("R8\t"+FRT.get("R8"));
	System.out.println("R9\t"+FRT.get("R9"));
	System.out.println("R10\t"+FRT.get("R10"));
	System.out.println("R11\t"+FRT.get("R11"));
	System.out.println("R12\t"+FRT.get("R12"));
	System.out.println("R13\t"+FRT.get("R13"));
	System.out.println("R14\t"+FRT.get("R14"));
	System.out.println("R15\t"+FRT.get("R15"));
	System.out.println("R16\t"+FRT.get("R16"));
	
	
	System.out.println("The contents of Back Rename table are:");
	System.out.println(BRT.values());
	
	
}

void Print_IQ(){
	System.out.println("Th contents of the IQ are as follows:");
	System.out.println("Total instructions remaining are: "+Iqueue.size());
	Instruction temp = null;
	
	for(int i=0;i<Iqueue.size();i++){	
		temp = new Instruction();
		if(Iqueue.get(i)!=null)
		temp=Iqueue.get(i);
		
		System.out.println(i+" Inctruction: "+temp.getInstruction());
	}	
}

void Print_ROB(){
	
}

void Print_URF(){
	
}

void Print_Memory(){
	System.out.println("Please enter the range start of the Memory:");
	Scanner s=new Scanner(System.in);
	int startMem=s.nextInt();
	System.out.println("Please enter the range end of the Memory:");
	s=new Scanner(System.in);
	int endMem=s.nextInt();
	
	System.out.println("\nThe memory contents are as below:");
	Mem[5]=3;
	for(int j=1;j<=endMem;j++)
	{
		System.out.print("\t Mem["+j*startMem+"]: "+Mem[j]);
	}
	
	
	/*for(int j=startMem;j<Mem.length&&j<=endMem;j++)
	{
		System.out.print("\t Mem["+j*4+"]: "+Mem[j]);
	}*/
}

void Print_Stats(){
	int newpc=PC-1;
	System.out.println("IPC realized upto now.."+newpc);
	
}

void Set_URF_size(int size){
	System.out.println("Size updated to: "+size);
}

}