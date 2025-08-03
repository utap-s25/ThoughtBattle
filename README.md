<p align="center">
  
  <img width="300" height="300" alt="ThoughtBatle" src="https://github.com/user-attachments/assets/aa833b92-f8e5-4de5-87a9-101b111de25c" />

</p>

<h1 align="center">🧠 Thought Battle</h1>

<p align="center">
  A virtual debate platform with AI-powered moderation and dynamic discussion features.
</p>

---
## 📽️ Demo Video

Watch the video walkthrough: [YouTube - Thought Battle Demo](https://www.youtube.com/watch?v=_KzZqu9kIsY)

**Timestamps:**

- `0:00` Authentication  
- `0:25` Home Page  
- `0:45` Joining a Debate Chat (with Moderator Bot)  
- `1:15` Auto-Generated Debate Info Section  
- `1:33` User Profile (no uploaded picture)  
- `1:39` Editing User Profile  
- `2:00` Creating Debate Chat  
- `2:19` Joining Newly Created Debate  
- `2:30` Auto-Generated Gemini Summary  
- `2:43` Updated User Profile with Debate History  
- `2:55` Mutual Shared State + Bot Demo 
## 🧩 Key Functionalities

### 🗣️ Public Debate Channels
- Create or join debates on any topic.
- Built using Sendbird’s public group channels.
- AI moderation and user role support.

### 👤 User Profile Management
- Firebase Firestore stores user info and debate history.
- Firebase Storage handles profile pictures.
- Displays debate history for joined/hosted debates.

### 🧠 Auto-Generated Information Tabs
- Gemini API generates summaries for each debate side.
- Populates info section dynamically per debate.

### 🤖 Discussion Moderator Bot
- Neutral AI bot moderates each chat.
- Asks relevant questions and ensures civil discourse.

---

## 🧰 Tech Stack & Tools

<ul>
  <li><strong>Language:</strong> Kotlin</li>
  <li><strong>Architecture:</strong> MVVM (Model-View-ViewModel)</li>
  <li><strong>IDE:</strong> Android Studio</li>
  <li><strong>Platform:</strong> Android</li>
</ul>
### 🔧 Major Libraries & Frameworks

- **Sendbird SDK & Chat Platform API**  
  Core real-time chat functionality, bot integration, and public group channel support.

- **Firebase (Auth, Firestore, Storage)**  
  Handles authentication, user data, profile images, and debate records.

- **Gemini API**  
  Generates concise summaries for both debate sides.

- **Material Design Components**  
  Used for consistent UI across buttons, icons, and cards.

- **Glide**  
  Smooth image loading and caching for user profiles.

- **Retrofit**  
  For interacting with external APIs (e.g., Sendbird Platform API).

---
