package com.example.arcorelab.common.helpers

import android.app.Activity
import com.google.ar.core.Config
import com.google.ar.core.Session

object SessionManagerHelper
{
    fun createSession(activity: Activity) :Session
    {
        // Create a new ARCore session.
        var session = Session(activity)
        return session
    }

    fun createConfig(session: Session) : Config
    {
        // Create a session config.
        val config = Config(session)
        // Do feature-specific operations here, such as enabling depth or turning on
        // support for Augmented Faces.
        return config
    }

    fun configurateSession(session: Session,config: Config) : Session
    {
        // Configure the session.
        session.configure(config)
        return session
    }

    fun closeSession(session: Session)
    {
        session.close()
    }




}