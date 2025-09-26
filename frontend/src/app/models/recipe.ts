export interface Ingredient {
name: string;
quantity?: string;
}


export interface Recipe {
id?: number | string;
title: string;
description?: string;
category?: 'Vegetarian' | 'Non-Vegetarian' | string;
timeMinutes?: number;
imageUrl?: string;
ingredients: Ingredient[];
steps: string[];
vegetarian?: boolean;
}