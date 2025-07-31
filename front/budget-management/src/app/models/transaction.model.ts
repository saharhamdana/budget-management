export interface Transaction {
  id: number;
  montant: number;
  dateOpertation: string;  // On utilise string car JSON renvoie généralement des dates en format ISO string
  dateValeur: string;
  description: string;
  reference: string;
  client: string;  // identifiant ou nom du client selon backend
  categorie?: Categorie; // facultatif si la catégorie peut être null
}

export interface Categorie {
  id: number;
  nom: string;
}
