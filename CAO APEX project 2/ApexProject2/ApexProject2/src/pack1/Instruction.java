package pack1;

public class Instruction {
	public String instruction;	
	public String str_rep_inst;
	public Integer mulcycle;
	public Integer getMulcycle() {
		return mulcycle;
	}

	public void setMulcycle(Integer mulcycle) {
		this.mulcycle = mulcycle;
	}

	public String getStr_rep_inst() {
		return str_rep_inst;
	}

	public void setStr_rep_inst(String str_rep_inst) {
		this.str_rep_inst = str_rep_inst;
	}

	public String FUType;
	public int src1ReadyBit;
	public String src1;
	public int src1Value;
	public int src2ReadyBit;
	public int getSrc2ReadyBit() {
		return src2ReadyBit;
	}

	public void setSrc2ReadyBit(int src2ReadyBit) {
		this.src2ReadyBit = src2ReadyBit;
	}

	public String src2;
	public int src2Value;
	public String dest;
	public String destReadyBit;
	public String destValue;
	public int status;
	
	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

	public String getFUType() {
		return FUType;
	}

	public void setFUType(String fUType) {
		FUType = fUType;
	}

	public int getSrc1ReadyBit() {
		return src1ReadyBit;
	}

	public void setSrc1ReadyBit(int src1ReadyBit) {
		this.src1ReadyBit = src1ReadyBit;
	}

	public String getSrc1() {
		return src1;
	}

	public void setSrc1(String src1) {
		this.src1 = src1;
	}

	public int getSrc1Value() {
		return src1Value;
	}

	public void setSrc1Value(int src1Value) {
		this.src1Value = src1Value;
	}

	public String getSrc2() {
		return src2;
	}

	public void setSrc2(String src2) {
		this.src2 = src2;
	}

	public int getSrc2Value() {
		return src2Value;
	}

	public void setSrc2Value(int src2Value) {
		this.src2Value = src2Value;
	}

	public String getDest() {
		return dest;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public String getDestReadyBit() {
		return destReadyBit;
	}

	public void setDestReadyBit(String destReadyBit) {
		this.destReadyBit = destReadyBit;
	}

	public String getDestValue() {
		return destValue;
	}

	public void setDestValue(String destValue) {
		this.destValue = destValue;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
