package com.vlocker.new_theme.b;

import android.view.View;
import android.view.View.OnClickListener;
import com.vlocker.new_theme.beans.e;
import com.vlocker.theme.c.a;

class s implements OnClickListener {
    final /* synthetic */ e a;
    final /* synthetic */ n b;

    s(n nVar, e eVar) {
        this.b = nVar;
        this.a = eVar;
    }

    public void onClick(View view) {
        a.b().a(this.a.h().get(0));
        this.b.a("设计师新品");
    }
}
