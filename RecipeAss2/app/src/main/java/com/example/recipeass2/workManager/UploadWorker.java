package com.example.recipeass2.workManager;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.recipeass2.database.AppDatabase;
import com.example.recipeass2.shoppingList.ShoppingListItem;
import com.example.recipeass2.shoppingList.ShoppingListItemDao;
import com.example.recipeass2.user.FavoriteRecipe;
import com.example.recipeass2.user.User;
import com.example.recipeass2.user.UserDao;
import com.example.recipeass2.user.UserFavoriteRecipeCrossRef;
import com.example.recipeass2.user.UserWithFavoriteRecipes;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UploadWorker extends Worker {
    public UploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Print the current timestamp and the method name
        Log.d("UploadWorker", "doWork: " + getFormattedTimestamp());

        try{
            // Get the UserDao
            UserDao userDao = AppDatabase.getDatabase(getApplicationContext()).userDao();

            // Get the ShoppingListItemDao
            ShoppingListItemDao shoppingListItemDao = AppDatabase.getDatabase(getApplicationContext()).shoppingListItemDao();

            // Get the user data
            List<User> users = userDao.getAllUsers(); // Modify this line according to your UserDao methods

            // Get the Firebase instance
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference usersRef  = database.getReference("users");

            // Add or update user data
            for (User user : users) {
                String emailPath = user.getEmail().replace('.', ',');
                usersRef .child(emailPath).setValue(user);

                // Get the user's favorite recipes
                UserWithFavoriteRecipes userWithFavoriteRecipes  = userDao.getUserWithFavoriteRecipesDirect(user.getEmail());
                // Add or update the user's favorite recipes
                for (FavoriteRecipe favoriteRecipe : userWithFavoriteRecipes.getFavoriteRecipes()) {
                    usersRef.child(emailPath).child("favoriteRecipes").child(String.valueOf(favoriteRecipe.getId())).setValue(favoriteRecipe);
                }

                // Get the user's shopping list items
                List<ShoppingListItem> shoppingListItems = shoppingListItemDao.getShoppingListItemsByUserEmailDirect(user.getEmail());
                // Add or update the user's shopping list items
                for (ShoppingListItem item : shoppingListItems) {
                    usersRef.child(emailPath).child("shoppingList").child(String.valueOf(item.getId())).setValue(item);
                }
            }
        }catch (Exception e) {
            Log.e("UploadWorker", "Error in doWork", e);
            return Result.failure();
        }

        Log.d("UploadWorker", "doWork completed: " + getFormattedTimestamp());
        return Result.success();
    }

    private String getFormattedTimestamp() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return simpleDateFormat.format(new Date());
    }
}
