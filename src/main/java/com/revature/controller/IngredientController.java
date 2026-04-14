package com.revature.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

import com.revature.model.Ingredient;
import com.revature.service.IngredientService;


/**
 * The IngredientController class handles operations related to ingredients. It allows for creating, retrieving, updating, and deleting individual ingredients, as well as retrieving a list of all ingredients. 
 * 
 * The class interacts with the IngredientService to perform these operations.
 */

public class IngredientController {

    /**
     * A service that manages ingredient-related operations.
     */

    @SuppressWarnings("unused")
    private IngredientService ingredientService;

    /**
     * Constructs an IngredientController with the specified IngredientService.
     *
     * TODO: Finish the implementation so that this class's instance variables are initialized accordingly.
     * 
     * @param ingredientService the service used to manage ingredient-related operations
     */

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    /**
     * TODO: Retrieves a single ingredient by its ID.
     * 
     * If the ingredient exists, responds with a 200 OK status and the ingredient data. If not found, responds with a 404 Not Found status.
     *
     * @param ctx the Javalin context containing the request path parameter for the ingredient ID
     */
    public void getIngredient(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));

        Ingredient ingredient = ingredientService.getIngredientById(id);

        if (ingredient != null) {
            ctx.json(ingredient);
            ctx.status(200);
        } else {
            ctx.status(404);
        }
    }

    /**
     * TODO: Deletes an ingredient by its ID.
     * 
     * Responds with a 204 No Content status.
     *
     * @param ctx the Javalin context containing the request path parameter for the ingredient id
     */
    public void deleteIngredient(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));

        ingredientService.deleteIngredient(id);

        ctx.status(204);
    }

    /**
     * TODO: Updates an existing ingredient by its ID.
     * 
     * If the ingredient exists, updates it and responds with a 204 No Content status. If not found, responds with a 404 Not Found status.
     *
     * @param ctx the Javalin context containing the request path parameter and updated ingredient data in the request body
     */
    public void updateIngredient(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Ingredient updatedIngredient = ctx.bodyAsClass(Ingredient.class);

        boolean updated = ingredientService.updateIngredient(id, updatedIngredient);

        if (updated) {
            ctx.status(204);
        } else {
            ctx.status(404);
        }
    }

    /**
     * TODO: Creates a new ingredient.
     * 
     * Saves the ingredient and responds with a 201 Created status.
     *
     * @param ctx the Javalin context containing the ingredient data in the request body
     */
    public void createIngredient(Context ctx) {
          Ingredient ingredient = ctx.bodyAsClass(Ingredient.class);

        ingredientService.createIngredient(ingredient);

        ctx.status(201);
    }

    /**
     * TODO: Retrieves a paginated list of ingredients, or all ingredients if no pagination parameters are provided.
     * 
     * If pagination parameters are included, returns ingredients based on page, page size, sorting, and filter term.
     *
     * @param ctx the Javalin context containing query parameters for pagination, sorting, and filtering
     */
    public void getIngredients(Context ctx) {
       Integer page = getParamAsClassOrElse(ctx, "page", Integer.class, null);
        Integer pageSize = getParamAsClassOrElse(ctx, "pageSize", Integer.class, null);
        String sort = getParamAsClassOrElse(ctx, "sort", String.class, null);
        String sortBy = getParamAsClassOrElse(ctx, "sortBy", String.class, null);
        String sortDirection = getParamAsClassOrElse(ctx, "sortDirection", String.class, null);
        String filter = getParamAsClassOrElse(ctx, "filter", String.class, null);
        String term = getParamAsClassOrElse(ctx, "term", String.class, null);
        
        // Use term if filter is not provided
        if (filter == null && term != null) {
            filter = term;
        }

        // If no pagination params (page, pageSize, sortBy) → return list
        if (page == null && pageSize == null && sortBy == null) {
            List<Ingredient> ingredients = (List<Ingredient>) ingredientService.getIngredients(null, null, null, filter);
            ctx.json(ingredients);
            ctx.status(200);
        } else {
            // Pagination + filtering
            String sortField = sort != null ? sort : (sortBy != null ? sortBy : "id");
            String sortDir = sortDirection != null ? sortDirection : "asc";
            Object result = ingredientService.searchIngredients(filter, page != null ? page : 1, pageSize != null ? pageSize : 10, sortField, sortDir);
            ctx.json(result);
            ctx.status(200);
        }
    }

    /**
     * A helper method to retrieve a query parameter from the context as a specific class type, or return a default value if the query parameter is not present.
     *
     * @param <T> the type of the query parameter
     * @param ctx the Javalin context containing query parameters
     * @param queryParam the name of the query parameter to retrieve
     * @param clazz the class type of the parameter
     * @param defaultValue the default value to return if the parameter is absent
     * @return the query parameter value as the specified type, or the default value if absent
     */
    private <T> T getParamAsClassOrElse(Context ctx, String queryParam, Class<T> clazz, T defaultValue) {
        if(ctx.queryParam(queryParam) != null) {
            return ctx.queryParamAsClass(queryParam, clazz).get();
        } else {
            return defaultValue;
        }
    }
    /**
     * Configure the routes for ingredient operations.
     *
     * @param app the Javalin application
     */
    public void configureRoutes(Javalin app) {
        app.get("/ingredients", this::getIngredients);
        app.get("/ingredients/{id}", this::getIngredient);
        app.post("/ingredients", this::createIngredient);
        app.put("/ingredients/{id}", this::updateIngredient);
        app.delete("/ingredients/{id}", this::deleteIngredient);
    }
}

