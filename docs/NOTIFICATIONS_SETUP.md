# Discord Notifications Setup Guide

This guide explains how to configure Discord notifications for the ImagePickerKMP CI/CD pipeline.

## Overview

The project includes Discord notifications for:

1. **Basic Notifications** - Integrated directly in the CI workflow
2. **Advanced Notifications** - Separate workflow with detailed coverage information

## Discord Setup

### 1. Create Discord Webhook

1. Go to your Discord server
2. Select the channel where you want notifications
3. Click the gear icon → "Integrations"
4. Click "Create Webhook"
5. Give it a name (e.g., "ImagePickerKMP CI")
6. Copy the webhook URL

### 2. Add GitHub Secret

1. Go to your GitHub repository
2. Navigate to Settings → Secrets and variables → Actions
3. Click "New repository secret"
4. Name: `DISCORD_WEBHOOK_URL`
5. Value: Paste your Discord webhook URL

## Notification Types

### Basic Notifications (CI Workflow)

These are triggered on every build:

- **Success**: 🎉 Build completed successfully
- **Failure**: ❌ Build failed

### Advanced Notifications (Notifications Workflow)

These provide detailed information:

- **Build Status**: Success/failure with detailed metrics
- **Coverage Reports**: Line and branch coverage percentages
- **Coverage Alerts**: Threshold-based alerts for low coverage

## Message Format

### Discord Messages

```
🎉 ImagePickerKMP Build Success

Repository: ismoy/ImagePickerKMP
Branch: main
Commit: abc123...
Triggered by: username

📊 Coverage Report:
• Line Coverage: 45.2%
• Branch Coverage: 32.1%

Duration: 120s
Workflow: CI

🔗 View Details: https://github.com/...
```

## Coverage Thresholds

The system includes automatic coverage alerts:

- **< 20%**: ⚠️ Low coverage warning
- **20-50%**: 📈 Coverage improving
- **≥ 50%**: 🎉 Excellent coverage

## Customization

### Modify Notification Channel

Edit `.github/workflows/ci.yml` and `.github/workflows/notifications.yml`:

```yaml
- name: Notify Discord on Success
  uses: Ilshidur/action-discord@master
  env:
    DISCORD_WEBHOOK: ${{ secrets.DISCORD_WEBHOOK_URL }}
  with:
    args: |
      Your custom message here...
```

### Add Custom Messages

You can customize the notification text by modifying the `args` field:

```yaml
args: |
  🚀 **Custom Build Success Message**
  
  **Project:** ImagePickerKMP
  **Status:** ${{ github.event.workflow_run.conclusion }}
  
  Your custom message here...
```

### Disable Notifications

To disable notifications temporarily, comment out the notification steps in the workflow files.

## Troubleshooting

### Notifications Not Working

1. **Check Secret**: Ensure `DISCORD_WEBHOOK_URL` is set correctly
2. **Verify Webhook URL**: Test webhook manually using curl or Postman
3. **Check Channel Permissions**: Ensure the webhook has access to the channel
4. **Review Workflow Logs**: Check GitHub Actions logs for error messages

### Test Webhook Manually

```bash
# Test Discord webhook
curl -X POST -H 'Content-type: application/json' \
  --data '{"content":"Test notification from ImagePickerKMP"}' \
  YOUR_DISCORD_WEBHOOK_URL
```

### Common Issues

1. **Invalid Webhook URL**: Double-check the URL format
2. **Channel Not Found**: Ensure the channel exists and the webhook has access
3. **Rate Limiting**: Discord has rate limits; notifications may be delayed
4. **Workflow Permissions**: Ensure the workflow has permission to send notifications

## Security Considerations

- **Webhook URLs are sensitive**: Never commit them to the repository
- **Use repository secrets**: Always store webhook URLs in GitHub secrets
- **Monitor usage**: Regularly check webhook usage to ensure it's working as expected

## Advanced Configuration

### Multiple Channels

You can send notifications to multiple channels by creating multiple webhooks:

```yaml
- name: Notify Discord - Development
  uses: Ilshidur/action-discord@master
  env:
    DISCORD_WEBHOOK: ${{ secrets.DISCORD_DEV_WEBHOOK_URL }}
  # ... other config

- name: Notify Discord - Production
  uses: Ilshidur/action-discord@master
  env:
    DISCORD_WEBHOOK: ${{ secrets.DISCORD_PROD_WEBHOOK_URL }}
  # ... other config
```

### Conditional Notifications

Send notifications only for specific conditions:

```yaml
- name: Notify on Main Branch Only
  if: github.ref == 'refs/heads/main'
  uses: Ilshidur/action-discord@master
  # ... notification config
```

### Custom Coverage Thresholds

Modify the coverage alert thresholds in `.github/workflows/notifications.yml`:

```yaml
${{ steps.coverage.outputs.line_coverage < 30 && '⚠️ **Low Coverage Alert!**' || '' }}
```

## Support

If you encounter issues with notifications:

1. Check the [GitHub Actions documentation](https://docs.github.com/en/actions)
2. Check the [Discord webhook documentation](https://discord.com/developers/docs/resources/webhook)
3. Open an issue in the repository for specific problems 