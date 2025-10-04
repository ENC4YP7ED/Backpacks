# Instructions to Push to Your GitHub Fork

## Step 1: Create a Fork on GitHub

1. Go to the original repository: https://github.com/Spydnel/Backpacks
2. Click the **"Fork"** button in the top-right corner
3. GitHub will create a fork under your account: `https://github.com/YOUR-USERNAME/Backpacks`

## Step 2: Configure Git User (One-time setup)

Before pushing, set your Git identity:

```bash
cd /home/x/Schreibtisch/Backpacks-master

# Set your name and email
git config user.name "Your Name"
git config user.email "your.email@example.com"
```

## Step 3: Add Your Fork as Remote

Replace `YOUR-USERNAME` with your actual GitHub username:

```bash
# Add your fork as the 'origin' remote
git remote add origin https://github.com/YOUR-USERNAME/Backpacks.git

# Verify the remote was added
git remote -v
```

## Step 4: Push to Your Fork

```bash
# Push to your fork's main/master branch
git push -u origin master
```

You'll be prompted for your GitHub credentials.

**Note:** If you have 2FA enabled, you'll need to use a Personal Access Token instead of your password:
1. Go to GitHub Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Generate new token with `repo` scope
3. Use the token as your password when prompted

## Step 5: Create a Pull Request (Optional)

If you want to contribute back to the original repository:

1. Go to your fork on GitHub
2. Click **"Pull requests"** → **"New pull request"**
3. Select the original repository as the base
4. Add a description of your changes
5. Submit the pull request

## Alternative: Using SSH (Recommended for regular use)

If you prefer SSH over HTTPS:

```bash
# Add your fork with SSH URL
git remote add origin git@github.com:YOUR-USERNAME/Backpacks.git

# Push with SSH
git push -u origin master
```

**Setup SSH key:**
1. Generate SSH key: `ssh-keygen -t ed25519 -C "your.email@example.com"`
2. Add to GitHub: Settings → SSH and GPG keys → New SSH key
3. Paste the contents of `~/.ssh/id_ed25519.pub`

## Quick Reference

```bash
# Check current status
git status

# View commit history
git log --oneline

# Push changes after making more commits
git push

# Pull changes from your fork
git pull

# Create a new branch for features
git checkout -b feature-name
git push -u origin feature-name
```

## Troubleshooting

### Authentication Failed
- Make sure you're using your GitHub username (not email)
- Use a Personal Access Token instead of password if 2FA is enabled

### Permission Denied
- Verify you've forked the repository to your account
- Check that the remote URL is correct: `git remote -v`

### Already Exists Error
- If you already have a repository named "Backpacks", rename it or use a different name for the fork

## Current Repository Status

✅ Git repository initialized
✅ All files committed with message
✅ Ready to push to your fork

**Current commit:**
- Branch: `master`
- Commit message: "Add Accessories mod integration and keybinding support"
- Files: 70 files changed, 3233 insertions(+)
