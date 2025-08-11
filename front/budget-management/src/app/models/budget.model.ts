import { Categorie } from "./categorie.model";

export interface Budget {
  id?: number;
  amountPerMonth: number;
  categorie: any;
  userId?: string;
  realAmount?: number;
  depassement?: boolean;
  valeurDepassement?: number;
  editing?: boolean;
}
