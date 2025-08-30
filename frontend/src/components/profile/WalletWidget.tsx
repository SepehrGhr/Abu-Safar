import React from 'react';
import { Wallet } from 'lucide-react';
import ActionButton from '../common/ActionButton';

interface WalletWidgetProps {
    balance: number;
    onAddFunds: () => void;
}

export default function WalletWidget({ balance, onAddFunds }: WalletWidgetProps) {
    return (
        <div className="bg-white/80 dark:bg-slate-900/50 backdrop-blur-lg rounded-2xl shadow-xl p-6 flex flex-col h-full">
            <div className="flex items-center mb-4">
                <Wallet className="text-passport-gold mr-3" />
                <h3 className="font-bold text-lg text-stone-800 dark:text-white">My Wallet</h3>
            </div>
            <div className="text-center flex-grow flex flex-col justify-center">
                <p className="text-4xl font-bold text-stone-900 dark:text-white">${balance.toFixed(2)}</p>
                <p className="text-sm text-stone-500 dark:text-stone-400">Available Balance</p>
            </div>
            <div className="mt-6">
                <ActionButton onClick={onAddFunds} className="w-full">
                    Add Funds
                </ActionButton>
            </div>
        </div>
    );
}