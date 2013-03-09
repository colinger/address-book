(ns address-book.auth
  (:use [sandbar.form-authentication]
          [sandbar.validation]))

(defrecord AuthAdapter []
    FormAuthAdapter
    (load-user [this username password]
      (cond (= username "admin")
            {:username "admin" :password "xinying" :roles #{:user}}))
  (validate-password [this]
    (fn [m]
      (if (= (:password m) "xinying")
        m
        (add-validation-error m "Unable to authenticate user.")))))

(defn form-authentication-adapter []
    (merge
      (AuthAdapter.)
      {:username "Username"
       :password "Password"
       :username-validation-error "You must supply a valid username."
       :password-validation-error "You must supply a password."
      :logout-page "/"}))

