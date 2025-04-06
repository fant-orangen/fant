export interface Category {
  id: string | number | null;
  name: string;
  imageUrl: string;
  parent?: Category | null;
  translationKey?: string;
}
