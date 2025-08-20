import React, { useState, useEffect } from 'react';
import { getCompaniesByVehicleType } from '/src/services/api/companies';
import type { Company } from '/src/services/api/types';

interface CompanyFilterProps {
  vehicleType: string;
}



const CompanyFilter: React.FC<CompanyFilterProps> = ({ vehicleType }) => {
  const [searchParams, setSearchParams] = useSearchParams();
  const [companies, setCompanies] = useState<Company[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Get selected companies from URL (e.g., "?company=Mahan,Fadak")
    const selectedCompanies = new Set(searchParams.get('companies')?.split(',') || []);

    const handleCompanyChange = (companyName: string, isChecked: boolean) => {
      const newSelectedCompanies = new Set(selectedCompanies);
      if (isChecked) {
        newSelectedCompanies.add(companyName);
      } else {
        newSelectedCompanies.delete(companyName);
      }

      const newSearchParams = new URLSearchParams(searchParams);
      if (newSelectedCompanies.size > 0) {
        newSearchParams.set('companies', Array.from(newSelectedCompanies).join(','));
      } else {
        newSearchParams.delete('companies'); // Clean up URL if no companies are selected
      }
      setSearchParams(newSearchParams);
    };


  useEffect(() => {
    if (vehicleType) {
      const fetchCompanies = async () => {
        setIsLoading(true);
        setError(null);
        try {
          const results = await getCompaniesByVehicleType(vehicleType);
          setCompanies(results);
        } catch (err) {
          setError('Failed to load companies.');
          console.error(err);
        } finally {
          setIsLoading(false);
        }
      };
      fetchCompanies();
    }
  }, [vehicleType]);

  return (
    <div className="mt-6">
      <h4 className="font-bold text-white mb-3">By Carrier</h4>
      <div className="space-y-2">
        {isLoading && <p className="text-white/50">Loading carriers...</p>}
        {error && <p className="text-red-400">{error}</p>}
        {!isLoading && !error && companies.map((company) => (
          <label key={company.id} className="flex items-center text-white/80">
            <input
              type="checkbox"
              className="rounded bg-transparent border-white/30 text-yellow-400 focus:ring-yellow-400"
              checked={selectedCompanies.has(company.name)}
              onChange={(e) => handleCompanyChange(company.name, e.target.checked)}
            />
            <span className="ml-3">{company.name}</span>
          </label>
        ))}
        {!isLoading && !error && companies.length === 0 && (
            <p className="text-white/50">No carriers found for this vehicle type.</p>
        )}
      </div>
    </div>
  );
};

export default CompanyFilter;