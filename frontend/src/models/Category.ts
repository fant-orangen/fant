export interface Category {
  id: string;
  name: string;
  description: string;
  parent?: Category | null;
}
