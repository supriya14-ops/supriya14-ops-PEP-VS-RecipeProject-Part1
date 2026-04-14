package com.revature;

import static org.mockito.Mockito.*;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.junit.jupiter.api.Test;

import com.revature.controller.RecipeController;
import com.revature.model.Recipe;
import com.revature.service.AuthenticationService;
import com.revature.service.RecipeService;
import com.revature.util.Page;

import java.util.Collections;
import java.util.List;
import java.util.Arrays;

public class RecipeControllerTest {

    @Test
    public void testGetRecipesWithRecipeName() throws Exception {
        RecipeService recipeService = mock(RecipeService.class);
        AuthenticationService authService = mock(AuthenticationService.class);
        Recipe mockRecipe = new Recipe("Grilled Cheese", "Grill bread and cheese");
        Page<Recipe> mockPage = new Page<>(1, 10, 1, 1, Collections.singletonList(mockRecipe));
        when(recipeService.searchRecipes(null, 1, 10, "id", "asc")).thenReturn(mockPage);

        Context ctx = mock(Context.class);
        when(ctx.queryParam("page")).thenReturn(null);
        when(ctx.queryParam("pageSize")).thenReturn(null);
        when(ctx.queryParam("sortBy")).thenReturn(null);
        when(ctx.queryParam("sortDirection")).thenReturn(null);
        when(ctx.queryParam("term")).thenReturn(null);

        Handler getRecipes = new RecipeController(recipeService, authService).fetchAllRecipes;
        getRecipes.handle(ctx);

        verify(ctx).status(200);
        verify(ctx).json(mockPage);
    }

    @Test
    public void testGetRecipesWithNoParams() throws Exception {
        RecipeService recipeService = mock(RecipeService.class);
        AuthenticationService authService = mock(AuthenticationService.class);
        List<Recipe> allRecipes = Arrays.asList(new Recipe("Apple Pie"), new Recipe("Grilled Cheese"),
                new Recipe("Steak"));
        Page<Recipe> mockPage = new Page<>(1, 10, 1, 3, allRecipes);
        when(recipeService.searchRecipes(null, 1, 10, "id", "asc")).thenReturn(mockPage);

        Context ctx = mock(Context.class);
        when(ctx.queryParam("page")).thenReturn(null);
        when(ctx.queryParam("pageSize")).thenReturn(null);
        when(ctx.queryParam("sortBy")).thenReturn(null);
        when(ctx.queryParam("sortDirection")).thenReturn(null);
        when(ctx.queryParam("term")).thenReturn(null);

        Handler getRecipesHandler = new RecipeController(recipeService, authService).fetchAllRecipes;
        getRecipesHandler.handle(ctx);

        verify(ctx).status(200); // Set the response status code
        verify(ctx).json(mockPage);
    }

    @Test
    public void testGetRecipesWithNoResults() throws Exception {
        RecipeService recipeService = mock(RecipeService.class);
        AuthenticationService authService = mock(AuthenticationService.class);
        when(recipeService.getRecipes(null, null, null, null)).thenReturn(Collections.emptyList());

        Context ctx = mock(Context.class);
        when(ctx.queryParam("page")).thenReturn(null);
        when(ctx.queryParam("size")).thenReturn(null);
        when(ctx.queryParam("sort")).thenReturn(null);
        when(ctx.queryParam("filter")).thenReturn(null);

        Handler getRecipes = new RecipeController(recipeService, authService).fetchAllRecipes;
        getRecipes.handle(ctx);

        verify(ctx).status(404);
        verify(ctx).result("No recipes found");
    }
}