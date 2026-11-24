export interface Account {
  id: number;
  accountNumber: string;
  balance: number;
  availableBalance: number;
  status: 'ACTIVE' | 'BLOCKED' | 'CLOSED';
}

export interface TransferRequest {
  sourceAccountId: number;
  destinationAccount: string;
  amount: string;
}

export interface Transfer extends Omit<TransferRequest, 'amount'> {
  id: string;
  destinationBank: 'HORIZONTE';
  amount: number;
  currency: 'AOA';
  status: 'PENDING' | 'COMPLETED' | 'FAILED';
  createdAt: string;
}

export interface CreditReceipt {
  id: string;
  destinationAccount: string;
  amount: number;
  currency: 'AOA';
  status: 'CREDITED' | 'REJECTED';
  createdAt: string;
}
