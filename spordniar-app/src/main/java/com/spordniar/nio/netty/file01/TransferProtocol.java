package com.spordniar.nio.netty.file01;

public class TransferProtocol {

	private TransferType transferType;
	
	private TransferBody transferBody;

	public TransferType getTransferType() {
		return transferType;
	}

	public void setTransferType(TransferType transferType) {
		this.transferType = transferType;
	}

	public TransferBody getTransferBody() {
		return transferBody;
	}

	public void setTransferBody(TransferBody transferBody) {
		this.transferBody = transferBody;
	}
	
}
