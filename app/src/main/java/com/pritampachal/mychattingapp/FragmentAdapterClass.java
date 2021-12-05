package com.pritampachal.mychattingapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

//below-all-code, type manually by me
public class FragmentAdapterClass extends FragmentStateAdapter {

    public FragmentAdapterClass(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ChatFragment();
            case 1:
                return new StatusFragment();
            case 2:
                return new CallFragment();
        }
        return new ChatFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
